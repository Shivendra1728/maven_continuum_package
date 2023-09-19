-- Useing Continuum Database 
USE continuum;

-- Inserting Roles Entries
INSERT INTO roles
        (role, created_date, updated_date, description, enabled, rolename, page_id)
		VALUES
            ('Admin', NOW(), NOW(), 'Admin role description', 1, 'admin_role', 1),
            ('Super_admin', NOW(), NOW(), 'Super_Admin role description', 1, 'super_admin_role', 1),
            ('Return_Processor', NOW(), NOW(), 'Return_Processor role description', 1, 'Return_Processor', 1),
            ('End_User', NOW(), NOW(), 'End_User role description', 1, 'End_User', 1);

-- Inserting User
INSERT INTO user
    (created_date, updated_date, email, enabled, first_name, gender, last_name, login_dt, password, status, 
    updated_dt, full_name, user_name, role_id)
VALUES  
    (NOW(), NOW(), 'Testuser@example.com', 1, 'Test', 1, 'User', NOW(), '$10$A53obZVu60Rts1z5nkGjyeol9EyZJuY4dq5dzd7vI8KIiiCbzHzhu', 1, 
    NOW(), 'Test User','TestUser7@tt.com' , 2);