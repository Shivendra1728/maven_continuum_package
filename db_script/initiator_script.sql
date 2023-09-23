-- Useing Continuum Database 
USE continuum;

CREATE TABLE tbl_tenant_master (
    tenant_client_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    db_name VARCHAR(255) NOT NULL,
    driver_class VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL
);

-- Inserting Driver name an JDBC URL for continuum Database
INSERT INTO tbl_tenant_master (db_name, driver_class, password, status, url, user_name)
VALUES ('continuum', 'com.mysql.cj.jdbc.Driver', 'root', 'active', 'jdbc:mysql://localhost:3306/continuum', 'root');

-- Inserting Reason codes for creating RMA
INSERT INTO reason_code(code, description, img_mandatory, is_pop_up )
VALUES ('Item Damaged In Transit', 'RMA - DAMAGED IN TRANSIT', 1, 0),
        ('Defective Item/ Poor Quality Item', 'RMA - DEFECTIVE ITEM/ OUT-OF-BOX FAILURE/ POOR QUALITY', 1, 0),
        ('Incorrect Item Ordered', 'RMA - INCORRECT ITEM ORDERED', 1, 0),
        ('Item did not match specifications', 'RMA - MANUFACTURER INCORRECT SPECIFICATIONS', 1, 0),
        ('Ordered too many', 'RMA - ORDER QUANTITY', 1, 0),
        ('Item received is not item ordered', 'RMA - SUPPLIER ERROR INCORRECT ITEM', 1, 0),
        ('Warranty/ Repair/ Replace', 'RMA - WARRANTY / REPAIR / REPLACE', 1, 0),
        ('Other (fill in issue)', '', 1, 0);

-- Inserting Roles Entries for Admin, Super_admin, Return_Processor, End_User
INSERT INTO roles
        (role, created_date, updated_date)
		VALUES
            ('ADMIN', NOW(), NOW()),
            ('ADMIN', NOW(), NOW()),
            ('Return_Processor', NOW(), NOW()),
            ('End_User', NOW(), NOW());

-- Inserting User Username : AdminUser@tt.com  Passsword : Admin@123
INSERT INTO user
    (created_date, updated_date, email, first_name, gender, last_name, login_dt, password, status, 
    updated_dt, full_name, user_name, role_id)
VALUES  
    (NOW(), NOW(), 'AdminTestuser@example.com', 'Admin', 1, 'User', NOW(), '$2a$10$0Y.miHQMvZZxqtt3xe/S5eE/ApSHdmVE4XHDFRCQW4ZrK7VaxgqXW', 1, 
    NOW(), 'Admin User','AdminUser@tt.com' , 2);