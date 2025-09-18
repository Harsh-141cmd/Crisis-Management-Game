# Crisis Communication Game - Enhanced UI Edition

An immersive, AI-powered crisis communication simulation that challenges players to navigate com- **Professional development recommendations
- **Contextual crisis visualizations**: Crisis-relevant images based on scenario type and performanceex corporate crises as executives at different leadership levels. The game features a sophisticated dark-themed UI with glassmorphism effects, smooth animations, and professional design elements inspired by modern portfolio websites.

## 🎯 Game Overview

Step into the shoes of a corporate executive facing real-world crisis scenarios. Make critical decisions, manage stakeholders, and navigate through complex challenges that test your communication and leadership skills across 10 intense decision rounds.

## 📸 Screenshots

### Desktop Experience
<div align="center">

**Intro Screen**
![Intro Screen](screenshots/Intro%20Screen.png)

**Game Setup Form** 
![Game Setup Form Screen](screenshots/Game%20Setup%20Form%20Screen.png)

**Loading Screen**
![Loading Screen](screenshots/Loading%20Screen.png)

**Game Interface**
![Game Interface Screen](screenshots/Game%20Interface%20Scrren.png)

**Results Analysis**
![Result Screen](screenshots/Result%20Screen.png)

</div>


## ✨ Enhanced UI Features

### Modern Design Elements
- **Glassmorphism Effects**: Translucent panels with backdrop blur for a premium feel
- **Dark Theme**: Professional dark color scheme with orange accent colors (#ff6b35)
- **Smooth Animations**: Fluid transitions, pulse effects, and micro-interactions
- **Responsive Layout**: Fully responsive design that works on all device sizes

### Interactive Components
- **Dynamic Header**: Shows game progress and real-time status indicators
- **Executive Setup Form**: Elegant profile creation with difficulty selection
- **Crisis Dashboard**: Live metrics showing reputation, decisions left, and stakeholder trust
- **Enhanced Narrative Display**: Typewriter effects and visual indicators for scenario updates
- **Interactive Decision Cards**: Hover effects, impact indicators, and selection feedback
- **Comprehensive Results**: Multi-tab analysis with skills assessment and growth recommendations

### Visual Enhancements
- **Floating Particles**: Animated background elements for atmosphere
- **Loading Animations**: Professional loading screens with orbital animations
- **Progress Indicators**: Visual progress bars and status indicators
- **AI-Generated Images**: Contextual crisis visualization using curated stock photos

## 🚀 Technology Stack

### Backend (Java)
- **Framework**: Pure Java with built-in HTTP server
- **Build Tool**: Maven
- **Java Version**: 17
- **Dependencies**: 
  - OkHttp 4.12.0 for HTTP client operations
  - Gson 2.11.0 for JSON processing
- **AI Integration**: Gemini 2.5 Pro API for narrative generation and Unsplash for contextual images

### Frontend (React)
- **Framework**: React 18 with Vite build tool
- **Styling**: Custom CSS with modern design patterns
- **Fonts**: Inter for UI text, Playfair Display for headings
- **Animations**: CSS animations and transitions
- **Communication**: REST API calls to Java backend

## 🎮 Game Mechanics

### Executive Levels (Difficulty 1-5)
1. **Intern Level**: PR Assistant / HR Assistant roles
2. **Manager Level**: PR Manager / HR Manager roles  
3. **Senior Level**: Head of Communications / HR roles
4. **Director Level**: VP Corporate Affairs / HR roles
5. **Executive Level**: CEO / CFO / CTO / CHRO roles

### Gameplay Flow
1. **Profile Setup**: Create your executive profile with personalized details
2. **Crisis Simulation**: Navigate through 10 turns of escalating crisis scenarios
3. **Decision Making**: Choose from 5 strategic options each turn
4. **Real-time Feedback**: See immediate consequences of your choices
5. **Comprehensive Analysis**: Receive detailed assessment of your performance

### Assessment Categories
- **Crisis Response**: How effectively you handle immediate threats
- **Stakeholder Management**: Your ability to manage different stakeholder groups
- **Decision Making**: Quality and speed of your strategic decisions
- **Media Relations**: Effectiveness in handling media and public communications
- **Team Leadership**: Your leadership style and team management
- **Strategic Thinking**: Long-term planning and strategic vision

## 🛠 Installation & Setup

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- OpenAI API key

### Backend Setup
```bash
cd backend
# Set environment variable for Gemini API
export GEMINI_API_KEY="your-gemini-api-key-here"
# Or on Windows:
set GEMINI_API_KEY=your-gemini-api-key-here

# Build and run
mvn clean compile
mvn exec:java
```

The backend will start on `http://localhost:8080`

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

## 🎨 Design Inspiration

The enhanced UI draws inspiration from modern portfolio designs, incorporating:

- **Glassmorphism**: Semi-transparent panels with backdrop filters
- **Smooth Animations**: Carefully crafted transitions and micro-interactions
- **Professional Color Palette**: Dark backgrounds with strategic orange accents
- **Typography Hierarchy**: Clear information hierarchy with modern fonts
- **Interactive Elements**: Hover states, loading indicators, and feedback systems
- **Responsive Grid System**: Adaptive layouts for all screen sizes

## 🔧 Configuration

### Environment Variables
- `GEMINI_API_KEY`: Your Google Gemini API key for AI-powered narratives

### Customization Options
- **Color Scheme**: Modify CSS variables in `enhanced-ui.css`
- **Animation Speed**: Adjust animation durations in CSS
- **Game Rules**: Modify difficulty settings and turn counts in components
- **AI Model**: Change Gemini model version in `GameService.java`

## 📊 Features Breakdown

### Crisis Dashboard
- Real-time reputation tracking
- Remaining decision counter
- Stakeholder trust metrics
- Visual crisis indicators

### Enhanced Narrative System
- Typewriter text effects
- Scenario badges and indicators
- Smooth scroll to content
- Visual feedback for AI processing

### Decision Interface
- Card-based option selection
- Impact prediction bars
- Selection confirmation
- Hover effects and animations

### Results Analysis
- Multi-tab interface (Overview, Analysis, Skills, Growth)
- Circular progress indicators
- Skills assessment with progress bars
- Professional development recommendations
- AI-generated result visualization

## 🌐 API Endpoints

- `POST /api/game/start`: Initialize new game session
- `POST /api/game/turn`: Process player choices and advance narrative
- `GET /api/ping`: Health check with CORS headers

## 📱 Responsive Design

The application is fully responsive with breakpoints for:
- **Desktop**: 1024px and above (full multi-column layouts)
- **Tablet**: 768px - 1023px (adapted grid systems)
- **Mobile**: 480px - 767px (single column, touch-optimized)
- **Small Mobile**: Below 480px (minimal layout, essential features)

## 📞 Support

For support, questions, or feedback, please open an issue in the GitHub repository.

## 🏗️ Contributors

- **Frontend Development**: [Harsh-141cmd](https://github.com/Harsh-141cmd)
- **Backend Development**: [desmonub](https://github.com/desmonub)

---

**Built with ❤️ for Crisis Management Professionals and Leadership Development by [Harsh-141cmd](https://github.com/Harsh-141cmd) and [desmonub](https://github.com/desmonub)**
