CREATE DATABASE IF NOT EXISTS servicesphere;
USE servicesphere;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- 4. Tickets Table (with agent assignment support)
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(50),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) DEFAULT 'Medium',
    status VARCHAR(20) DEFAULT 'OPEN',
    category VARCHAR(50),
    requester_id BIGINT,
    assigned_agent_id BIGINT NULL,
    assigned_agent_name VARCHAR(100) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Assets Table (with assigned user support)
CREATE TABLE IF NOT EXISTS assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) NOT NULL UNIQUE,
    asset_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    status VARCHAR(20) DEFAULT 'Available',
    location VARCHAR(100),
    assigned_to_user VARCHAR(100) NULL
);

-- 6. Knowledge Articles Table
CREATE TABLE IF NOT EXISTS knowledge_articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50)
);

-- =========================================================================
-- SEED SAMPLE DATA FOR ALL 5 ROLES & WORKFLOWS
-- =========================================================================
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'EMPLOYEE'), (2, 'AGENT'), (3, 'MANAGER'), (4, 'ASSET_MANAGER'), (5, 'ADMIN');

INSERT IGNORE INTO departments (id, name) VALUES
(1, 'Engineering'), (2, 'Human Resources'), (3, 'IT Support');

INSERT IGNORE INTO users (id, full_name, email, password_hash, role_id, department_id, status) VALUES
(1, 'Demo Employee', 'employee@servicesphere.com', 'pass123', 1, 1, 'ACTIVE'),
(2, 'Sarah Support Agent', 'agent@servicesphere.com', 'pass123', 2, 3, 'ACTIVE'),
(3, 'Mark Lead Manager', 'manager@servicesphere.com', 'pass123', 3, 3, 'ACTIVE'),
(4, 'Alex Asset Manager', 'assetmgr@servicesphere.com', 'pass123', 4, 3, 'ACTIVE'),
(5, 'System Admin', 'admin@servicesphere.com', 'pass123', 5, 3, 'ACTIVE');

INSERT IGNORE INTO tickets (id, ticket_number, title, description, priority, status, category, requester_id) VALUES
(1, 'INC-1001', 'VPN Connection Failure', 'Unable to connect to corporate VPN after the recent network security update.', 'High', 'OPEN', 'Networking', 1),
(2, 'INC-1002', 'Outlook Email Sync Error', 'Emails are failing to sync on the Outlook desktop client.', 'Medium', 'IN_PROGRESS', 'Software', 1),
(3, 'INC-1003', 'New Developer Laptop Request', 'Need a standard engineering laptop pre-configured with Java and Node.js toolchain.', 'Low', 'OPEN', 'Hardware', 1);

INSERT IGNORE INTO assets (id, asset_tag, asset_type, brand, model, status, location) VALUES
(1, 'AST-001', 'Laptop', 'Dell', 'Latitude 5520', 'Assigned', 'Floor 3 - Engineering Bay'),
(2, 'AST-002', 'Monitor', 'HP', 'P24h G4 24-inch', 'Available', 'IT Storage Room B'),
(3, 'AST-003', 'Software License', 'JetBrains', 'IntelliJ IDEA Ultimate', 'Active', 'Corporate License Pool');

INSERT IGNORE INTO knowledge_articles (id, title, content, category) VALUES
(1, 'How to Connect to Corporate VPN', '1. Open GlobalProtect VPN client. \n2. Enter gateway address: vpn.servicesphere.com. \n3. Authenticate using your company SSO credentials.', 'Networking'),
(2, 'Fixing Outlook Sync Issues', 'If Outlook fails to sync, clear your local credential manager cache, restart the application, and verify your network connection.', 'Software'),
(3, 'Password Reset Guidelines', 'To reset your enterprise password, navigate to the internal identity portal or contact the IT Service Desk at extension 4357.', 'Security');