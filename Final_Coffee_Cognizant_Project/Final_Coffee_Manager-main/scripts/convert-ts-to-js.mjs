import { promises as fs } from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { transform } from '@swc/core';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const projectRoot = path.resolve(__dirname, '..');

const shouldSkip = (file) => {
  if (file.endsWith('.d.ts')) return true;
  // Keep node_modules and dist out
  if (file.includes(`${path.sep}node_modules${path.sep}`)) return true;
  if (file.includes(`${path.sep}dist${path.sep}`)) return true;
  return false;
};

async function* walk(dir) {
  const entries = await fs.readdir(dir, { withFileTypes: true });
  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      // Skip some directories
      if (['node_modules', '.git', 'dist'].includes(entry.name)) continue;
      yield* walk(fullPath);
    } else {
      yield fullPath;
    }
  }
}

function getOutPath(file) {
  if (file.endsWith('.tsx')) return file.replace(/\.tsx$/, '.jsx');
  if (file.endsWith('.ts')) return file.replace(/\.ts$/, '.js');
  return file;
}

function swcOptions(isTsx) {
  return {
    jsc: {
      parser: {
        syntax: 'typescript',
        tsx: isTsx,
        decorators: false,
        dynamicImport: true,
      },
      target: 'es2020',
      transform: {
        // Preserve JSX in .jsx output; bundler will handle it
      },
      loose: false,
    },
    module: {
      type: 'es6',
    },
    sourceMaps: false,
    swcrc: false,
  };
}

function postProcess(file, code) {
  // Special-case: vite config might import a type-only Plugin
  if (file.endsWith('vite.config.js')) {
    code = code.replace(/import\s+\{\s*defineConfig\s*,\s*Plugin\s*\}\s*from\s*["']vite["'];?/, "import { defineConfig } from 'vite';");
    code = code.replace(/import\s+\{\s*Plugin\s*\}\s*from\s*["']vite["'];?\n?/, '');
    // Remove leftover type annotations in comments (unlikely after swc)
  }
  return code;
}

async function convertFile(file) {
  if (!file.endsWith('.ts') && !file.endsWith('.tsx')) return false;
  if (shouldSkip(file)) return false;

  const isTsx = file.endsWith('.tsx');
  const outFile = getOutPath(file);

  const src = await fs.readFile(file, 'utf8');
  const { code } = await transform(src, swcOptions(isTsx));
  const processed = postProcess(path.basename(outFile), code);
  await fs.writeFile(outFile, processed, 'utf8');
  await fs.unlink(file);
  return { in: file, out: outFile };
}

async function run() {
  const changed = [];
  for await (const file of walk(projectRoot)) {
    if (file.endsWith('.ts') || file.endsWith('.tsx')) {
      const res = await convertFile(file);
      if (res) changed.push(res);
    }
  }
  // Log summary
  console.log(`Converted ${changed.length} files`);
  for (const c of changed) {
    console.log(`${path.relative(projectRoot, c.in)} -> ${path.relative(projectRoot, c.out)}`);
  }
}

run().catch((err) => {
  console.error(err);
  process.exit(1);
});
