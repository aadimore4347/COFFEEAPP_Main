# Coffee Machine Management System - Frontend

## 🎯 **Overview**
A modern React frontend application for monitoring and managing coffee machines across multiple facilities. Features real-time MQTT sensor data, role-based access control, and comprehensive analytics.

## 🚀 **Features**

### **Authentication & Authorization**
- JWT-based authentication with automatic token refresh
- Role-based access control (ADMIN/FACILITY)
- Secure login and registration system
- Demo mode fallback when backend is unavailable

### **Real-time Monitoring**
- Live MQTT sensor data from coffee machines
- Real-time water, milk, beans, and temperature levels
- Machine status monitoring (ON/OFF/ERROR)
- Automatic data refresh every 30-60 seconds

### **Machine Management**
- Comprehensive machine overview and analytics
- Supply level monitoring with visual indicators
- Refill functionality for low supplies
- Usage statistics and performance metrics
- Maintenance tracking and alerts

### **Role-based Dashboards**
- **Admin Dashboard**: System-wide overview, facility management, user management
- **Facility Dashboard**: Facility-specific machine monitoring and analytics
- **Machine Dashboard**: Individual machine details and controls

### **Analytics & Reporting**
- Real-time performance metrics
- Supply level trends and alerts
- Machine efficiency tracking
- Usage statistics and consumption data

## 🏗️ **Architecture**

### **Frontend Stack**
- **React 18** with functional components and hooks
- **Vite** for fast development and building
- **Tailwind CSS** for responsive styling
- **Radix UI** for accessible components
- **React Router** for navigation
- **React Query** for data management

### **Integration Points**
- **Backend API**: Spring Boot REST API (localhost:8080)
- **MQTT Worker**: Real-time sensor data service (localhost:8081)
- **Real-time Updates**: WebSocket-like polling for live data

## 📁 **Project Structure**

```
frontend/
├── client/                    # Main React application
│   ├── components/           # Reusable UI components
│   │   ├── ui/              # Radix UI components
│   │   ├── RealTimeAnalytics.jsx  # Real-time dashboard
│   │   ├── AddMachineModal.jsx    # Machine creation
│   │   └── ...              # Other components
│   ├── pages/               # Application pages
│   │   ├── CorporateDashboard.jsx # Admin dashboard
│   │   ├── Login.jsx        # Authentication
│   │   ├── MachineManagement.jsx  # Machine overview
│   │   └── ...              # Other pages
│   ├── lib/                 # Utility libraries
│   │   ├── backendApi.js    # Backend API client
│   │   ├── realTimeMqtt.js  # MQTT integration
│   │   ├── api.js           # Legacy API client
│   │   └── ...              # Other utilities
│   ├── contexts/            # React contexts
│   │   └── AuthContext.jsx  # Authentication context
│   ├── config/              # Configuration files
│   └── global.css           # Global styles
├── public/                  # Static assets
├── package.json             # Dependencies and scripts
├── vite.config.js           # Vite configuration
└── tailwind.config.js       # Tailwind CSS configuration
```

## 🔧 **Setup & Installation**

### **Prerequisites**
- Node.js 18+ and npm
- Backend API running on localhost:8080
- MQTT Worker service running on localhost:8081

### **Installation**
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### **Environment Variables**
Create a `.env` file in the frontend directory:
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_MQTT_WORKER_URL=http://localhost:8081/api
VITE_DEBUG=true
```

## 🌐 **Available Scripts**

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run test` - Run tests
- `npm run format.fix` - Format code with Prettier

## 🔌 **API Integration**

### **Backend API Endpoints**
- **Authentication**: `/api/auth/login`, `/api/auth/register`
- **Machines**: `/api/machine/*`
- **Admin**: `/api/admin/*`
- **Facility**: `/api/facility/*`
- **Health**: `/api/health/*`

### **MQTT Worker Endpoints**
- **Simulator**: `/api/simulator/*`
- **Health**: `/api/simulator/health`

### **Real-time Data Topics**
- `coffeeMachine/{id}/temperature`
- `coffeeMachine/{id}/waterLevel`
- `coffeeMachine/{id}/milkLevel`
- `coffeeMachine/{id}/beansLevel`
- `coffeeMachine/{id}/status`
- `coffeeMachine/{id}/usage`

## 👥 **User Roles**

### **ADMIN**
- Access to all facilities and machines
- Create/delete facilities and users
- System-wide analytics and performance metrics
- MQTT simulator controls
- Data refresh every 60 seconds

### **FACILITY**
- Access to machines within assigned facility
- Monitor machine status and sensor data
- Basic machine management
- Data refresh every 30 seconds

## 📊 **Real-time Features**

### **Sensor Data**
- **Water Level**: 0-100% with decreasing values
- **Milk Level**: 0-100% with decreasing values
- **Beans Level**: 0-100% with decreasing values
- **Temperature**: 22-98°C based on machine status

### **Alerts & Notifications**
- Low supply warnings (< 20%)
- High temperature alerts (> 95°C)
- Machine error notifications
- Connection status indicators

### **Data Refresh**
- Automatic updates every 30-60 seconds
- Manual refresh buttons
- Real-time connection monitoring
- Graceful error handling

## 🎨 **UI Components**

### **Core Components**
- **Cards**: Information display with consistent styling
- **Tables**: Sortable data tables for machine lists
- **Charts**: Real-time performance graphs
- **Modals**: Forms for machine/facility management
- **Alerts**: Toast notifications and status messages

### **Design System**
- **Color Scheme**: Professional blue/indigo gradients
- **Status Colors**: Green (operational), Yellow (warning), Red (error)
- **Responsive Layout**: Mobile-first design approach
- **Accessibility**: ARIA labels and keyboard navigation

## 🧪 **Testing**

### **Test Coverage**
- Component unit tests
- API integration tests
- User workflow testing
- Error handling scenarios
- Performance testing

### **Running Tests**
```bash
npm run test
```

## 🚀 **Deployment**

### **Production Build**
```bash
npm run build
```

### **Deploy Options**
- **Static Hosting**: Netlify, Vercel, GitHub Pages
- **Container**: Docker with nginx
- **CDN**: CloudFront, CloudFlare

## 🔒 **Security Features**

- JWT token authentication
- Role-based access control
- Secure token storage
- Automatic token refresh
- CSRF protection
- Input validation

## 📱 **Responsive Design**

- Mobile-first approach
- Tablet and desktop optimization
- Touch-friendly interfaces
- Adaptive layouts
- Progressive enhancement

## 🚨 **Error Handling**

- Network error recovery
- Graceful degradation
- User-friendly error messages
- Offline mode indicators
- Retry mechanisms

## 🔄 **Data Flow**

```
User Action → Frontend → Backend API → Database
     ↓
MQTT Simulator → MQTT Worker → Backend API → Database
     ↓
Frontend ← Real-time Updates ← MQTT Worker
```

## 📈 **Performance Features**

- Lazy loading for large datasets
- Optimized re-renders
- Efficient data structures
- Debounced search and filtering
- Memory leak prevention

## 🎯 **Future Enhancements**

- **Export Functionality**: PDF reports and data export
- **Notification System**: Email/SMS alerts
- **Mobile App**: React Native version
- **Advanced Analytics**: ML-powered insights
- **Multi-language Support**: Internationalization
- **Offline Mode**: Service worker implementation

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 **License**

This project is licensed under the MIT License.

## 🆘 **Support**

For support and questions:
- Check the documentation
- Review existing issues
- Create a new issue with detailed information

---

**Built with ❤️ using React, Tailwind CSS, and modern web technologies**