import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Coffee, User, Building, Lock, Eye, EyeOff, MapPin, Mail } from "lucide-react";

export default function Signup() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [formData, setFormData] = useState({
    username: "",
    name: "",
    email: "", // Changed from gmail
    password: "",
    confirmPassword: "",
    role: "FACILITY", // Default to FACILITY
    facilityId: null
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (formData.password !== formData.confirmPassword) {
      return setError("Passwords do not match");
    }
    if (formData.password.length < 6) {
      return setError("Password must be at least 6 characters long");
    }

    setIsLoading(true);

    // Prepare data for the backend
    const userData = {
      username: formData.username,
      password: formData.password,
      // The backend expects role and facilityId, not name, email etc. on the base registration
      // This might need adjustment based on the DTO, but we'll stick to the core fields.
      // The backend RegisterRequest DTO has: username, password, role, facilityId
      role: formData.role,
      facilityId: formData.role === 'FACILITY' ? formData.facilityId : null,
    };

    try {
      const registrationSuccess = await register(userData);
      if (registrationSuccess) {
        setSuccess("Registration successful! Redirecting to login...");
        setTimeout(() => {
          navigate("/login");
        }, 2000);
      } else {
        setError("Registration failed. Username might already exist.");
      }
    } catch (err) {
      setError(err.message || "An unexpected error occurred during registration.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRoleChange = (role) => {
    setFormData((prev) => ({ ...prev, role, facilityId: null }));
  };

  const handleFacilityChange = (facilityId) => {
    setFormData((prev) => ({ ...prev, facilityId: Number(facilityId) }));
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 to-amber-50 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 p-3 bg-orange-100 rounded-full w-fit"><Coffee className="h-8 w-8 text-orange-600" /></div>
          <CardTitle className="text-2xl font-bold text-gray-900">Create Your Account</CardTitle>
          <CardDescription className="text-gray-600">Join the CoffeeFlow management system</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (<Alert variant="destructive"><AlertDescription>{error}</AlertDescription></Alert>)}
            {success && (<Alert className="border-green-200 bg-green-50 text-green-800"><AlertDescription>{success}</AlertDescription></Alert>)}

            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input id="username" name="username" type="text" placeholder="Enter a username" value={formData.username} onChange={handleInputChange} required disabled={isLoading} />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <div className="relative">
                <Input id="password" name="password" type={showPassword ? "text" : "password"} placeholder="Enter your password" value={formData.password} onChange={handleInputChange} required disabled={isLoading} />
                <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 hover:text-gray-600">{showPassword ? <EyeOff /> : <Eye />}</button>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <Input id="confirmPassword" name="confirmPassword" type="password" placeholder="Confirm your password" value={formData.confirmPassword} onChange={handleInputChange} required disabled={isLoading} />
            </div>

            <div className="space-y-2">
              <Label htmlFor="role">Role</Label>
              <Select value={formData.role} onValueChange={handleRoleChange} disabled={isLoading}>
                <SelectTrigger><SelectValue placeholder="Select your role" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="FACILITY">Facility Manager</SelectItem>
                  <SelectItem value="ADMIN">Admin</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {formData.role === 'FACILITY' && (
              <div className="space-y-2">
                <Label htmlFor="facilityId">Facility</Label>
                <Select value={formData.facilityId?.toString()} onValueChange={handleFacilityChange} disabled={isLoading}>
                  <SelectTrigger><SelectValue placeholder="Select a facility" /></SelectTrigger>
                  <SelectContent>
                    {/* In a real app, this would be fetched from an API */}
                    <SelectItem value="1">Downtown Office</SelectItem>
                    <SelectItem value="2">Tech Campus</SelectItem>
                    <SelectItem value="3">Branch Office</SelectItem>
                    <SelectItem value="4">Research Center</SelectItem>
                    <SelectItem value="5">Remote Hub</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            )}

            <Button type="submit" className="w-full bg-orange-600 hover:bg-orange-700" disabled={isLoading}>{isLoading ? "Creating Account..." : "Create Account"}</Button>
          </form>
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">Already have an account?{" "}<Link to="/login" className="font-medium text-orange-600 hover:text-orange-700">Sign in here</Link></p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
