# Store Operation Management System - JavaFX GUI

A JavaFX-based store operation management system for Golden Hour watch retail outlets.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Running the Application](#running-the-application)
- [Building the Application](#building-the-application)
- [Troubleshooting](#troubleshooting)
- [Project Structure](#project-structure)
- [Features](#features)

---

## Prerequisites

Before you begin, ensure you have the following installed on your system:

### 1. Java Development Kit (JDK) 17 or higher
- Download from:  [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
- Verify installation: 
  ```bash
  java -version
  ```
  Should show version 17 or higher. 

### 2. Apache Maven 3.6 or higher
- Download from: [Maven Official Website](https://maven.apache.org/download.cgi)
- Verify installation:
  ```bash
  mvn -version
  ```

### 3. Git (Optional, for cloning the repository)
- Download from: [Git Official Website](https://git-scm.com/downloads)
- Verify installation: 
  ```bash
  git --version
  ```

---

## Project Setup

### Step 1: Clone or Download the Repository

**Option A: Using Git**
```bash
git clone https://github.com/mwikacha/system_operation_management_fop.git
cd system_operation_management_fop
```

**Option B: Download ZIP**
1. Download the ZIP file from GitHub
2. Extract to your desired location
3. Open terminal/command prompt in the extracted folder

### Step 2: Verify Project Structure

Ensure your project has this structure:
```
system_operation_management_fop/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── my/edu/wix1002/goldenhour/
│   │   └── resources/
│   │       └── fxml/
│   └── test/
├── data/
│   ├── employees.csv
│   ├── outlets.csv
│   └── models. csv
└── README.md
```

### Step 3: Install Dependencies

Run this command to download all required JavaFX and project dependencies: 

```bash
mvn clean install
```

This will: 
- Clean any previous builds
- Download JavaFX libraries
- Compile the project
- Run tests (if any)
- Package the application

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Running the Application

### Method 1: Using Maven JavaFX Plugin (Recommended)

```bash
mvn clean javafx:run
```

This command will:
1. Clean previous builds
2. Compile the code
3. Launch the JavaFX application

### Method 2: Compile and Run Separately

**Step 1: Compile the project**
```bash
mvn clean compile
```

**Step 2: Run the application**
```bash
mvn javafx:run
```

### Method 3: Run with GUI Application Launcher

If you're using an IDE with JavaFX support:

**IntelliJ IDEA:**
1. Open the project
2. Navigate to `src/main/java/my/edu/wix1002/goldenhour/ui/MainApp.java`
3. Right-click → Run 'MainApp. main()'

**Eclipse:**
1. Open the project
2. Right-click on the project → Run As → Maven Build
3. In "Goals", enter: `javafx:run`
4. Click Run

**VS Code:**
1. Install "Extension Pack for Java" and "Maven for Java"
2. Open the project
3. Press F5 or use Run → Start Debugging
4. Select "Java" as the environment

---

## Building the Application

### Create a JAR file

```bash
mvn clean package
```

The JAR file will be created in:  `target/goldenhour-1.0-SNAPSHOT.jar`

### Create an executable JAR with dependencies

```bash
mvn clean package assembly:single
```

---

## Troubleshooting

### Issue 1: "mvn: command not found"

**Solution:** Maven is not installed or not in your PATH. 
- **Windows:** Add Maven's `bin` directory to PATH environment variable
- **Mac/Linux:** Add to `.bashrc` or `.zshrc`:
  ```bash
  export PATH=$PATH:/path/to/maven/bin
  ```

### Issue 2: JavaFX modules not found

**Error message:**
```
Error: JavaFX runtime components are missing
```

**Solution:**
1. Check your `pom.xml` includes JavaFX dependencies
2. Run with the Maven plugin:
   ```bash
   mvn javafx:run
   ```

### Issue 3: Java version mismatch

**Error message:**
```
java.lang.UnsupportedClassVersionError
```

**Solution:**
- Ensure you're using JDK 17 or higher: 
  ```bash
  java -version
  ```
- Update JAVA_HOME environment variable if needed

### Issue 4: "Error: Could not find or load main class"

**Solution:**
1. Clean and rebuild:
   ```bash
   mvn clean install
   ```
2. Verify the main class in `pom.xml`:
   ```xml
   <mainClass>my.edu.wix1002.goldenhour.ui.MainApp</mainClass>
   ```

### Issue 5: CSV files not found

**Error message:**
```
FileNotFoundException: data/employees.csv
```

**Solution:**
- Ensure the `data/` folder exists in your project root
- Create required CSV files:
  ```bash
  mkdir data
  ```
- Add sample data files (see [Data Files](#data-files) section)

### Issue 6: Port/Display issues on Linux

**Error message:**
```
Could not initialize class com.sun.glass.ui.gtk.GtkApplication
```

**Solution (Linux only):**
```bash
export _JAVA_AWT_WM_NONREPARENTING=1
mvn javafx:run
```

---

## Project Structure

### Source Code
```
src/main/java/my/edu/wix1002/goldenhour/
├── ui/                          # JavaFX Controllers
│   ├── MainApp.java             # Application entry point
│   ├── LoginController.java     # Login screen
│   ├── DashboardController.java # Main dashboard
│   ├── RegisterEmployeeController.java
│   ├── SalesController.java
│   └── StockController.java
├── model/                       # Data models
│   ├── Employee.java
│   ├── Outlet. java
│   ├── Model.java
│   └── Sales.java
├── util/                        # Utility classes
│   └── DataLoader.java
├── StorageSystem/               # Data persistence
│   └── StoreManager.java
├── AttendanceSystem. java
├── StockManagement.java
└── salesSystem. java
```

### Resources
```
src/main/resources/
└── fxml/                        # JavaFX FXML files
    ├── Login.fxml
    ├── Dashboard.fxml
    ├── RegisterEmployee.fxml
    ├── SalesView.fxml
    └── StockView.fxml
```

### Data Files

Create these CSV files in the `data/` folder:

**data/employees.csv**
```csv
EmployeeID,EmployeeName,Role,Password,OutletCode
C60001,Tan Guan Han,Manager,password123,C60
C60002,John Doe,Full-time,pass456,C60
```

**data/outlets.csv**
```csv
OutletCode,OutletName,Location
C60,Kuala Lumpur City Centre,Kuala Lumpur
```

**data/models.csv**
```csv
ModelName,Price
Rolex Submariner,45000
Omega Seamaster,28000
```

---

## Features

### For Managers
- **Login** with Manager credentials
- **Register New Employees**
  - Validate Employee ID format
  - Check for duplicate IDs
  - Set role (Full-time/Part-time)
- **Exit** the application

### For Employees
- **Clock In/Out** - Track attendance
- **View Stock** - Check inventory levels
- **Record Sales** - Create new sales records
- **Logout** - Return to login screen

---

## Maven Commands Reference

| Command | Description |
|---------|-------------|
| `mvn clean` | Delete the `target/` folder |
| `mvn compile` | Compile the source code |
| `mvn test` | Run unit tests |
| `mvn package` | Create JAR file |
| `mvn install` | Install to local Maven repository |
| `mvn javafx:run` | Run the JavaFX application |
| `mvn clean javafx:run` | Clean and run |
| `mvn dependency:tree` | Show dependency tree |
| `mvn clean install -DskipTests` | Build without running tests |

---

## Important Notes

### 1. Don't Commit `target/` Folder
The `target/` folder contains compiled files and should be in `.gitignore`:

```gitignore
# . gitignore
target/
. idea/
*.iml
. vscode/
. DS_Store
```

### 2. First-Time Setup
When running for the first time, Maven will download dependencies.  This may take a few minutes depending on your internet connection.

### 3. Default Login Credentials
- **Manager Account:**
  - Employee ID: `C60001`
  - Password: `password123`

- **Employee Account:**
  - Employee ID: `C60002`
  - Password: `pass456`

### 4. Java Version
This project requires **Java 17** or higher due to JavaFX 21 compatibility.

---

## System Requirements

### Minimum Requirements
- **OS:** Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM:** 2 GB
- **Disk Space:** 500 MB (including Maven dependencies)
- **Display:** 1024x768 resolution

### Recommended Requirements
- **OS:** Windows 11, macOS 12+, or Linux (Ubuntu 22.04+)
- **RAM:** 4 GB or more
- **Disk Space:** 1 GB
- **Display:** 1920x1080 resolution

---

## Support & Contact

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review Maven output for specific error messages
3. Create an issue on the GitHub repository

---

## License

This project is developed for educational purposes as part of WIX1002 Fundamentals of Programming. 

---

## Contributors

- **Project Team:** Golden Hour Store Management System
- **Institution:** University of Malaya
- **Course:** WIX1002 - Fundamentals of Programming

---

## Changelog

### Version 1.0.0
- Initial release with JavaFX GUI
- Manager and Employee dashboards
- Employee registration
- Attendance tracking
- Stock management
- Sales recording

---

**Last Updated:** December 2025
