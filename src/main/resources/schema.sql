--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5 (Ubuntu 10.5-2.pgdg14.04+1)
-- Dumped by pg_dump version 11.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;
SET TIME ZONE 'UTC';

ALTER TABLE IF EXISTS ONLY elections.vote
  DROP CONSTRAINT IF EXISTS vote_voter_reg_id_fkey;
ALTER TABLE IF EXISTS ONLY elections.vote
  DROP CONSTRAINT IF EXISTS vote_candidate_reg_id_fkey;
ALTER TABLE IF EXISTS ONLY elections.election_registry
  DROP CONSTRAINT IF EXISTS election_registry_voter_id_fkey;
ALTER TABLE IF EXISTS ONLY elections.election_registry
  DROP CONSTRAINT IF EXISTS election_registry_election_id_fkey;
ALTER TABLE IF EXISTS ONLY elections.election
  DROP CONSTRAINT IF EXISTS election_created_by;
DROP TRIGGER IF EXISTS validate_user_trigger ON elections.election_user;
DROP TRIGGER IF EXISTS validate_registry_trigger ON elections.election_registry;
DROP TRIGGER IF EXISTS validate_election_trigger ON elections.election;
DROP TRIGGER IF EXISTS validate_candidate_trigger ON elections.election_registry;
DROP INDEX IF EXISTS elections.user_id_uindex;
DROP INDEX IF EXISTS elections.election_user_student_id_uindex;
DROP INDEX IF EXISTS elections.election_id_uindex;
ALTER TABLE IF EXISTS ONLY elections.vote
  DROP CONSTRAINT IF EXISTS vote_voter_reg_id_candidate_reg_id_key;
ALTER TABLE IF EXISTS ONLY elections.vote
  DROP CONSTRAINT IF EXISTS vote_pkey;
ALTER TABLE IF EXISTS ONLY elections.election_user
  DROP CONSTRAINT IF EXISTS user_pk;
ALTER TABLE IF EXISTS ONLY elections.election_registry
  DROP CONSTRAINT IF EXISTS election_registry_voter_id_election_id_key;
ALTER TABLE IF EXISTS ONLY elections.election_registry
  DROP CONSTRAINT IF EXISTS election_registry_pkey;
ALTER TABLE IF EXISTS ONLY elections.election
  DROP CONSTRAINT IF EXISTS election_pk;
ALTER TABLE IF EXISTS elections.vote
  ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS elections.election_user
  ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS elections.election_registry
  ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS elections.election
  ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS elections.vote_id_seq;
DROP TABLE IF EXISTS elections.vote;
DROP SEQUENCE IF EXISTS elections.user_id_seq;
DROP TABLE IF EXISTS elections.election_user;
DROP SEQUENCE IF EXISTS elections.election_registry_id_seq;
DROP TABLE IF EXISTS elections.election_registry;
DROP SEQUENCE IF EXISTS elections.election_id_seq;
DROP TABLE IF EXISTS elections.election;
DROP FUNCTION IF EXISTS elections.validate_vote();
DROP FUNCTION IF EXISTS elections.validate_user();
DROP FUNCTION IF EXISTS elections.validate_registry();
DROP FUNCTION IF EXISTS elections.validate_election();
DROP FUNCTION IF EXISTS elections.validate_candidate();
DROP SCHEMA IF EXISTS elections;
--
-- Name: elections; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA elections;


--
-- Name: validate_candidate(); Type: FUNCTION; Schema: elections; Owner: -
--

CREATE FUNCTION elections.validate_candidate() RETURNS trigger
  LANGUAGE plpgsql
AS
$$
begin
  assert NEW.is_candidate = false
    or NEW.is_candidate = true and
       (select count(*)
        from election el
        where el.id = NEW.election_id
          and el.candidate_deadline >= now()) = 1, 'Deadline has passed';
  return NEW;
end;
$$;


--
-- Name: validate_election(); Type: FUNCTION; Schema: elections; Owner: -
--

CREATE FUNCTION elections.validate_election() RETURNS trigger
  LANGUAGE plpgsql
AS
$$
begin
  assert NEW.id is not null,'Id cannot be null';
  assert NEW.candidate_deadline < NEW.date_start, 'Date start is before deadline for candidacy';
  assert NEW.date_start < NEW.date_end, 'Date end should be after date start';
  assert (select role from election_user where election_user.id = NEW.created_by) = 'ADMIN', 'Creator should be admin';
  assert NEW.description is not NULL, 'Description cannot be null';
  return NEW;
end;
$$;


--
-- Name: validate_registry(); Type: FUNCTION; Schema: elections; Owner: -
--

CREATE FUNCTION elections.validate_registry() RETURNS trigger
  LANGUAGE plpgsql
AS
$$
begin
  assert (select election_user.role from election_user where election_user.id = NEW.voter_id) =
         'USER', 'Only users with role USER can participate in election';
  assert (select candidate_deadline from election where election.id = NEW.election_id) >
         now(), 'The deadline of election initialization is over';
  return NEW;
end;
$$;


--
-- Name: validate_user(); Type: FUNCTION; Schema: elections; Owner: -
--

CREATE FUNCTION elections.validate_user() RETURNS trigger
  LANGUAGE plpgsql
AS
$$
begin
  assert NEW.last_name similar to
         '(([A-Z]\.?\s?)*([A-Z][a-z]+\.?\s?)+([A-Z]\.?\s?[a-z]*)*)', 'Name has to start with upper case';
  assert NEW.first_name similar to
         '(([A-Z]\.?\s?)*([A-Z][a-z]+\.?\s?)+([A-Z]\.?\s?[a-z]*)*)', 'Name has to start with upper case';
  assert NEW.password is not null, 'Password cannot be null';
  assert NEW.id is not null, 'ID cannot be null';
  assert NEW.role similar to 'USER|ADMIN', 'Role should be USER or ADMIN';
  assert NEW.student_id similar to '[a-z]{2}[0-9]{6}', 'Invalid student id';
  assert (select count(*) from election_user where student_id = NEW.student_id) =
         0, 'Student with provided is registered';
  return NEW;
end;
$$;


--
-- Name: validate_vote(); Type: FUNCTION; Schema: elections; Owner: -
--

CREATE FUNCTION elections.validate_vote() RETURNS trigger
  LANGUAGE plpgsql
AS
$$
begin
  assert (select count(*)
          from election_registry reg
          where reg.id in (NEW.voter_reg_id, NEW.candidate_reg_id)
          group by reg.election_id) = 1 , 'Voter and candidate are from different elections';
  assert (select is_candidate
          from election_registry reg
          where reg.id = NEW.candidate_reg_id), 'The target is not a candidate';
  return NEW;
end;
$$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: election; Type: TABLE; Schema: elections; Owner: -
--

CREATE TABLE elections.election
(
  id                 integer                     NOT NULL,
  date_start         timestamp without time zone NOT NULL,
  date_end           timestamp without time zone NOT NULL,
  candidate_deadline timestamp without time zone NOT NULL,
  created_by         integer                     NOT NULL,
  description        text
);


--
-- Name: election_id_seq; Type: SEQUENCE; Schema: elections; Owner: -
--

CREATE SEQUENCE elections.election_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


--
-- Name: election_id_seq; Type: SEQUENCE OWNED BY; Schema: elections; Owner: -
--

ALTER SEQUENCE elections.election_id_seq OWNED BY elections.election.id;


--
-- Name: election_registry; Type: TABLE; Schema: elections; Owner: -
--

CREATE TABLE elections.election_registry
(
  id           integer               NOT NULL,
  voter_id     integer               NOT NULL,
  election_id  integer               NOT NULL,
  is_candidate boolean DEFAULT false NOT NULL
);


--
-- Name: election_registry_id_seq; Type: SEQUENCE; Schema: elections; Owner: -
--

CREATE SEQUENCE elections.election_registry_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


--
-- Name: election_registry_id_seq; Type: SEQUENCE OWNED BY; Schema: elections; Owner: -
--

ALTER SEQUENCE elections.election_registry_id_seq OWNED BY elections.election_registry.id;


--
-- Name: election_user; Type: TABLE; Schema: elections; Owner: -
--

CREATE TABLE elections.election_user
(
  id         integer                NOT NULL,
  first_name character varying(256) NOT NULL,
  last_name  character varying(256) NOT NULL,
  student_id character varying(16)  NOT NULL,
  password   text                   NOT NULL,
  role       character varying(16)  NOT NULL
);


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: elections; Owner: -
--

CREATE SEQUENCE elections.user_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: elections; Owner: -
--

ALTER SEQUENCE elections.user_id_seq OWNED BY elections.election_user.id;


--
-- Name: vote; Type: TABLE; Schema: elections; Owner: -
--

CREATE TABLE elections.vote
(
  id               integer NOT NULL,
  voter_reg_id     integer NOT NULL,
  candidate_reg_id integer NOT NULL
);


--
-- Name: vote_id_seq; Type: SEQUENCE; Schema: elections; Owner: -
--

CREATE SEQUENCE elections.vote_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


--
-- Name: vote_id_seq; Type: SEQUENCE OWNED BY; Schema: elections; Owner: -
--

ALTER SEQUENCE elections.vote_id_seq OWNED BY elections.vote.id;


--
-- Name: election id; Type: DEFAULT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election
  ALTER COLUMN id SET DEFAULT nextval('elections.election_id_seq'::regclass);


--
-- Name: election_registry id; Type: DEFAULT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_registry
  ALTER COLUMN id SET DEFAULT nextval('elections.election_registry_id_seq'::regclass);


--
-- Name: election_user id; Type: DEFAULT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_user
  ALTER COLUMN id SET DEFAULT nextval('elections.user_id_seq'::regclass);


--
-- Name: vote id; Type: DEFAULT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.vote
  ALTER COLUMN id SET DEFAULT nextval('elections.vote_id_seq'::regclass);


--
-- Name: election election_pk; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election
  ADD CONSTRAINT election_pk PRIMARY KEY (id);


--
-- Name: election_registry election_registry_pkey; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_registry
  ADD CONSTRAINT election_registry_pkey PRIMARY KEY (id);


--
-- Name: election_registry election_registry_voter_id_election_id_key; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_registry
  ADD CONSTRAINT election_registry_voter_id_election_id_key UNIQUE (voter_id, election_id);


--
-- Name: election_user user_pk; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_user
  ADD CONSTRAINT user_pk PRIMARY KEY (id);


--
-- Name: vote vote_pkey; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.vote
  ADD CONSTRAINT vote_pkey PRIMARY KEY (id);


--
-- Name: vote vote_voter_reg_id_candidate_reg_id_key; Type: CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.vote
  ADD CONSTRAINT vote_voter_reg_id_candidate_reg_id_key UNIQUE (voter_reg_id, candidate_reg_id);


--
-- Name: election_id_uindex; Type: INDEX; Schema: elections; Owner: -
--

CREATE UNIQUE INDEX election_id_uindex ON elections.election USING btree (id);


--
-- Name: election_user_student_id_uindex; Type: INDEX; Schema: elections; Owner: -
--

CREATE UNIQUE INDEX election_user_student_id_uindex ON elections.election_user USING btree (student_id);


--
-- Name: user_id_uindex; Type: INDEX; Schema: elections; Owner: -
--

CREATE UNIQUE INDEX user_id_uindex ON elections.election_user USING btree (id);


--
-- Name: election_registry validate_candidate_trigger; Type: TRIGGER; Schema: elections; Owner: -
--

CREATE TRIGGER validate_candidate_trigger
  BEFORE UPDATE
  ON elections.election_registry
  FOR EACH ROW
EXECUTE PROCEDURE elections.validate_candidate();


--
-- Name: election validate_election_trigger; Type: TRIGGER; Schema: elections; Owner: -
--

CREATE TRIGGER validate_election_trigger
  BEFORE INSERT OR UPDATE
  ON elections.election
  FOR EACH ROW
EXECUTE PROCEDURE elections.validate_election();


--
-- Name: election_registry validate_registry_trigger; Type: TRIGGER; Schema: elections; Owner: -
--

CREATE TRIGGER validate_registry_trigger
  BEFORE INSERT OR UPDATE
  ON elections.election_registry
  FOR EACH ROW
EXECUTE PROCEDURE elections.validate_registry();


--
-- Name: election_user validate_user_trigger; Type: TRIGGER; Schema: elections; Owner: -
--

CREATE TRIGGER validate_user_trigger
  BEFORE INSERT OR UPDATE
  ON elections.election_user
  FOR EACH ROW
EXECUTE PROCEDURE elections.validate_user();


--
-- Name: election election_created_by; Type: FK CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election
  ADD CONSTRAINT election_created_by FOREIGN KEY (created_by) REFERENCES elections.election_user (id);


--
-- Name: election_registry election_registry_election_id_fkey; Type: FK CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_registry
  ADD CONSTRAINT election_registry_election_id_fkey FOREIGN KEY (election_id) REFERENCES elections.election (id);


--
-- Name: election_registry election_registry_voter_id_fkey; Type: FK CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.election_registry
  ADD CONSTRAINT election_registry_voter_id_fkey FOREIGN KEY (voter_id) REFERENCES elections.election_user (id);


--
-- Name: vote vote_candidate_reg_id_fkey; Type: FK CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.vote
  ADD CONSTRAINT vote_candidate_reg_id_fkey FOREIGN KEY (candidate_reg_id) REFERENCES elections.election_registry (id);


--
-- Name: vote vote_voter_reg_id_fkey; Type: FK CONSTRAINT; Schema: elections; Owner: -
--

ALTER TABLE ONLY elections.vote
  ADD CONSTRAINT vote_voter_reg_id_fkey FOREIGN KEY (voter_reg_id) REFERENCES elections.election_registry (id);


--
-- PostgreSQL database dump complete
--

--- ADMIN USER
SET SEARCH_PATH TO elections;
INSERT INTO elections.election_user (first_name, last_name, student_id, password, role) VALUES ('Admin', 'Admin', 'aa000000', '$2a$12$XcW3ZxZCPeX4Kxd5lenZV.Z9WqCGpYSMFo9q4W63dLiJHSD5jYwyS', 'ADMIN');

