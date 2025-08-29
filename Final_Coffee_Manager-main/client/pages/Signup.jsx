import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Coffee, User, Building, Lock, Eye, EyeOff, MapPin, Mail } from "lucide-react";

export default function Signup() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: "", name: "", gmail: "", password: "", confirmPassword: "", role: "", city: "", officeName: "" });
  const officeOptions = { pune: ["Hinjewadi IT Park", "Koregaon Park Corporate", "Viman Nagar Tech Hub"], mumbai: ["Bandra Kurla Complex", "Lower Parel Financial", "Andheri Tech Center"] };
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) return setError("Passwords do not match");
    if (formData.password.length < 6) return setError("Password must be at least 6 characters long");
    if (!formData.role) return setError("Please select a role");
    if (!formData.gmail) return setError("Please enter your Gmail address");
    if (!formData.gmail.includes("@gmail.com")) return setError("Please enter a valid Gmail address");
    if (formData.role === "technician") {
      if (!formData.city) return setError("Please select a city");
      if (!formData.officeName) return setError("Please select an office");
    }
    setIsLoading(true); setError("");
    try {
      const userData = { username: formData.username, name: formData.name, gmail: formData.gmail, password: formData.password, role: formData.role, city: formData.role === "admin" ? null : formData.city, officeName: formData.role === "admin" ? null : formData.officeName, registeredAt: new Date().toISOString() };
      const existingUsers = JSON.parse(localStorage.getItem("registeredUsers") || "[]");
      if (existingUsers.find((user) => user.username === formData.username)) { setError("Username already exists. Please choose a different one."); setIsLoading(false); return; }
      existingUsers.push(userData); localStorage.setItem("registeredUsers", JSON.stringify(existingUsers));
      setSuccess("Registration successful! You can now login."); setTimeout(() => { navigate("/login"); }, 2000);
    } catch (error) { setError("Registration failed. Please try again."); } finally { setIsLoading(false); }
  };

  const handleInputChange = (e) => { const { name, value } = e.target; setFormData((prev) => ({ ...prev, [name]: value })); if (error) setError(""); };
  const handleCityChange = (city) => { setFormData((prev) => ({ ...prev, city, officeName: "" })); if (error) setError(""); };
  const handleRoleChange = (role) => { setFormData((prev) => ({ ...prev, role, city: role === "admin" ? "" : prev.city, officeName: role === "admin" ? "" : prev.officeName })); if (error) setError(""); };
  const handleOfficeChange = (officeName) => { setFormData((prev) => ({ ...prev, officeName })); if (error) setError(""); };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 to-amber-50 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 p-3 bg-orange-100 rounded-full w-fit"><Coffee className="h-8 w-8 text-orange-600" /></div>
          <CardTitle className="text-2xl font-bold text-gray-900">Join CoffeeFlow</CardTitle>
          <CardDescription className="text-gray-600">Register to access the CoffeeFlow management system</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (<Alert variant="destructive"><AlertDescription>{error}</AlertDescription></Alert>)}
            {success && (<Alert className="border-green-200 bg-green-50 text-green-800"><AlertDescription>{success}</AlertDescription></Alert>)}
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <div className="relative">
                <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input id="username" name="username" type="text" placeholder="Enter your username" value={formData.username} onChange={handleInputChange} className="pl-10" required disabled={isLoading} />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="name">Full Name</Label>
              <div className="relative">
                <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input id="name" name="name" type="text" placeholder="Enter your full name" value={formData.name} onChange={handleInputChange} className="pl-10" required disabled={isLoading} />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="gmail">Gmail</Label>
              <div className="relative">
                <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input id="gmail" name="gmail" type="email" placeholder="Enter your Gmail address" value={formData.gmail} onChange={handleInputChange} className="pl-10" required disabled={isLoading} />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="role">Role</Label>
              <div className="relative">
                <User className="absolute left-3 top-3 h-4 w-4 text-gray-400 z-10" />
                <Select value={formData.role} onValueChange={handleRoleChange} disabled={isLoading}>
                  <SelectTrigger className="pl-10"><SelectValue placeholder="Select your role" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="technician">Technician</SelectItem>
                    <SelectItem value="admin">Admin</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            {formData.role === "technician" && (<>
              <div className="space-y-2">
                <Label htmlFor="city">City</Label>
                <div className="relative">
                  <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400 z-10" />
                  <Select value={formData.city} onValueChange={handleCityChange} disabled={isLoading}>
                    <SelectTrigger className="pl-10"><SelectValue placeholder="Select your city" /></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="pune">Pune</SelectItem>
                      <SelectItem value="mumbai">Mumbai</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="officeName">Office</Label>
                <div className="relative">
                  <Building className="absolute left-3 top-3 h-4 w-4 text-gray-400 z-10" />
                  <Select value={formData.officeName} onValueChange={handleOfficeChange} disabled={isLoading || !formData.city}>
                    <SelectTrigger className="pl-10"><SelectValue placeholder={formData.city ? "Select your office" : "Select city first"} /></SelectTrigger>
                    <SelectContent>
                      {formData.city && officeOptions[formData.city]?.map((office) => (<SelectItem key={office} value={office}>{office}</SelectItem>))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </>)}
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input id="password" name="password" type={showPassword ? "text" : "password"} placeholder="Enter your password" value={formData.password} onChange={handleInputChange} className="pl-10 pr-10" required disabled={isLoading} />
                <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-3 h-4 w-4 text-gray-400 hover:text-gray-600">{showPassword ? <EyeOff /> : <Eye />}</button>
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input id="confirmPassword" name="confirmPassword" type={showConfirmPassword ? "text" : "password"} placeholder="Confirm your password" value={formData.confirmPassword} onChange={handleInputChange} className="pl-10 pr-10" required disabled={isLoading} />
                <button type="button" onClick={() => setShowConfirmPassword(!showConfirmPassword)} className="absolute right-3 top-3 h-4 w-4 text-gray-400 hover:text-gray-600">{showConfirmPassword ? <EyeOff /> : <Eye />}</button>
              </div>
            </div>
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
