CREATE TABLE person(
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE OneToMany_actor(
    svnr INTEGER NOT NULL,
    firstname VARCHAR(200) NOT NULL,
    surname VARCHAR(200) NOT NULL,
    PRIMARY KEY (svnr)
);

CREATE TABLE OneToMany_role(
    id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    actor_id INTEGER NOT NULL,
    PRIMARY KEY (id)
);