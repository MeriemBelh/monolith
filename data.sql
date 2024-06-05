CREATE TABLE IF NOT EXISTS menuitems (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(255),
                                         description VARCHAR(255),
                                         price DOUBLE,
                                         category VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      status VARCHAR(255),
                                      customer VARCHAR(255),
                                      contents VARCHAR(1024) -- Assuming the contents of the order will be stored as a string
);
