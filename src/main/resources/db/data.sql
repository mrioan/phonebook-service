-- Sample Users

-- password is 123123
INSERT INTO USER(USERNAME, PASSWORD, ENABLED)
  select 'peter@email.com', '$2a$06$Vfwfs5hcvPIMb8XTPK5Wz.L4aalS0NGSe.xsHkRZHh.9qleYbONKS', true
  where NOT EXISTS (
      SELECT 1 FROM USER u WHERE u.USERNAME = 'peter@email.com'
  )
;

-- password is 123123
INSERT INTO USER(USERNAME, PASSWORD, ENABLED)
  select 'john@email.com', '$2a$06$Vfwfs5hcvPIMb8XTPK5Wz.L4aalS0NGSe.xsHkRZHh.9qleYbONKS', false
  where NOT EXISTS (
      SELECT 1 FROM USER u WHERE u.USERNAME = 'john@email.com'
  )
;

-- Sample Contact
insert into CONTACT(USER_ID, FIRST_NAME, LAST_NAME)
  select u.ID, 'John', 'Doe'
  from USER u where u.USERNAME = 'peter@email.com'
  and NOT EXISTS (
      SELECT 1 FROM contact c WHERE c.FIRST_NAME = 'John' and c.LAST_NAME = 'Doe'
  )
;

-- Sample Phone numbers

insert into PHONE(TYPE, NUMBER, CONTACT_ID) select 'Mobile', '+1 123 456 8900', c.ID
from CONTACT c where c.FIRST_NAME = 'John' and c.LAST_NAME = 'Doe'
and NOT EXISTS (
  SELECT 1 FROM phone p WHERE p.number = '+1 123 456 8900' and p.CONTACT_ID = c.id
);

insert into PHONE(TYPE, NUMBER, CONTACT_ID) select 'Home', '+1 123 456 8901', c.ID
from CONTACT c where c.FIRST_NAME = 'John' and c.LAST_NAME = 'Doe'
and NOT EXISTS (
  SELECT 1 FROM phone p WHERE p.number = '+1 123 456 8901' and p.CONTACT_ID = c.id
);


