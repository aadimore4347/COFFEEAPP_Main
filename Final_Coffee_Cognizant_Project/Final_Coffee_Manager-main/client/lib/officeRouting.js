// Utility functions for office-specific routing (JavaScript)

export function officeNameToPath(officeName) {
  return officeName
    .toLowerCase()
    .replace(/\s+/g, "-")
    .replace(/[^a-z0-9\-]/g, "")
    .replace(/-+/g, "-")
    .replace(/^-|-$/g, "");
}

const OFFICE_PATH_TO_NAME = {
  "hinjewadi-it-park": "Hinjewadi IT Park",
  "koregaon-park-corporate": "Koregaon Park Corporate",
  "viman-nagar-tech-hub": "Viman Nagar Tech Hub",
  "bandra-kurla-complex": "Bandra Kurla Complex",
  "lower-parel-financial": "Lower Parel Financial",
  "andheri-tech-center": "Andheri Tech Center",
};

export function pathToOfficeName(path) {
  return (
    OFFICE_PATH_TO_NAME[path] ||
    path
      .split("-")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ")
  );
}

export const OFFICE_LOCATIONS = {
  pune: [
    "Hinjewadi IT Park",
    "Koregaon Park Corporate",
    "Viman Nagar Tech Hub",
  ],
  mumbai: [
    "Bandra Kurla Complex",
    "Lower Parel Financial",
    "Andheri Tech Center",
  ],
};

export function getAllOfficePaths() {
  const allOffices = [...OFFICE_LOCATIONS.pune, ...OFFICE_LOCATIONS.mumbai];
  return allOffices.map(officeNameToPath);
}

export function isValidOfficePath(path) {
  return getAllOfficePaths().includes(path);
}
