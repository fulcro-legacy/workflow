CREATE SEQUENCE pet_id_seq;
CREATE TABLE pet (
  id                 INTEGER DEFAULT nextval('pet_id_seq') PRIMARY KEY ,
  name          VARCHAR(200) NOT NULL
);

