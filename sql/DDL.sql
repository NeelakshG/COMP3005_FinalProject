not neel
neelypeely223
Online

not neel
 changed the group name: 3005 final project. Edit Group — 2025-11-19 12:00 PM
not neel
 started a call that lasted an hour. — 2025-11-19 12:01 PM
not neel — 2025-11-19 12:03 PM
https://docs.google.com/document/d/18jO63SFnj6gjn_ks-m3S2keHbn5kr0TFzZA4Z18LQw0/edit?usp=sharing
Google Docs
final project 3005
celina — 2025-11-23 6:58 PM
@EgoLapse are you done the er model
EgoLapse — 2025-11-23 8:59 PM
almost, not home rn will probably finish tmr morning
not neel — 2025-11-24 2:22 PM
any update?
EgoLapse — 2025-11-24 4:16 PM
done
Image
not neel — 2025-11-24 6:33 PM
@EgoLapse its looking good and i can definitely start the SQL code now. I think it's missing  the relationship types though. Celina updated the doc so ill use that for reference when I code but do you think you could add that to the ER model?
EgoLapse — 2025-11-24 8:27 PM
yea i could
not neel — 2025-11-25 7:32 PM
here is the SQL code for the tables
CREATE TABLE Member (
    member_id      SERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    phone          VARCHAR(20) UNIQUE,
    gender         VARCHAR(20)
Expand
DDL.sql
6 KB
celina — 2025-11-28 1:02 PM
i updated some of the attributes, check the doc over and revise it into the ER model and i’ll probably keep revising if necessary
celina — Yesterday at 12:22 AM
@EgoLapse not to be disrespectful but like you need to finish the er model based on the changes we’ve added and also implement the relationships. you have a simple task to do while neel and i are working on multiple and harder tasks. our deadline is monday and you have not updated us on the er model which shouldn’t be difficult whatsoever. this final project is worth 30% on our final and i don’t know about you but i want a good mark on this.
not neel — Yesterday at 12:24 AM
make sure to check out the rubric for the grading scheme, theres a couple things you gotta add
not neel — Yesterday at 3:19 PM
@EgoLapse hello? Me and celina have done 95% of the work and you don't even have the decency to respond. we are seriously thinking about kicking you out. lock in. The final is due soon and we still have to record.
not neel — Yesterday at 5:21 PM
@EgoLapse
﻿
CREATE TABLE Member (
    member_id      SERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    phone          VARCHAR(20) UNIQUE,
    gender         VARCHAR(20)
);


CREATE TABLE Trainer (
    trainer_id     SERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    specialization VARCHAR(100)
);


CREATE TABLE AdministrativeStaff (
    admin_id  SERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) UNIQUE NOT NULL
);


CREATE TABLE Room (
    room_id   SERIAL PRIMARY KEY,
    name      VARCHAR(100) UNIQUE NOT NULL,
    capacity  INT CHECK (capacity > 0)
);


CREATE TABLE Class (
    class_id   SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    trainer_id INT REFERENCES Trainer(trainer_id) ON DELETE RESTRICT,
    room_id    INT REFERENCES Room(room_id) ON DELETE RESTRICT,
    date       DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    capacity   INT NOT NULL CHECK (capacity > 0),

    CONSTRAINT class_time_check CHECK (start_time < end_time)
);


CREATE TABLE FitnessGoal (
    goal_id       SERIAL PRIMARY KEY,
    member_id     INT REFERENCES Member(member_id) ON DELETE CASCADE,
    target_weight NUMERIC(5,2),
    start_date    DATE,
    end_date      DATE,
    status        VARCHAR(50),
    goal_type     VARCHAR(50)
);


CREATE TABLE HealthMetrics (
    metric_id           SERIAL PRIMARY KEY,
    member_id           INT REFERENCES Member(member_id) ON DELETE CASCADE,
    weight              NUMERIC(5,2),
    heartrate           INT,
    body_fat_percentage NUMERIC(5,2),
    recorded_at         TIMESTAMP DEFAULT NOW()
);


CREATE TABLE ClassRegistration (
    registration_id SERIAL PRIMARY KEY,
    member_id       INT REFERENCES Member(member_id) ON DELETE CASCADE,
    class_id        INT REFERENCES Class(class_id) ON DELETE CASCADE,
    registered_date TIMESTAMP DEFAULT NOW(),

    CONSTRAINT unique_class_signup UNIQUE(member_id, class_id)
);


CREATE TABLE PTSession (
    session_id     SERIAL PRIMARY KEY,
    member_id      INT REFERENCES Member(member_id) ON DELETE CASCADE,
    trainer_id     INT REFERENCES Trainer(trainer_id) ON DELETE RESTRICT,
    room_id        INT REFERENCES Room(room_id) ON DELETE RESTRICT,
    date           DATE NOT NULL,
    start_time     TIME NOT NULL,
    end_time       TIME NOT NULL,
    registered_date TIMESTAMP DEFAULT NOW(),

    CONSTRAINT ptsession_time_check CHECK (start_time < end_time)
);


CREATE TABLE TrainerAvailability (
    availability_id SERIAL PRIMARY KEY,
    trainer_id      INT REFERENCES Trainer(trainer_id) ON DELETE CASCADE,
    day             VARCHAR(20) NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,

    CONSTRAINT availability_time_check CHECK (start_time < end_time)
);


CREATE TABLE Equipment (
    equipment_id    SERIAL PRIMARY KEY,
    room_id         INT REFERENCES Room(room_id) ON DELETE SET NULL,
    name            VARCHAR(100),
    type            VARCHAR(50),
    operational_status VARCHAR(20)
);


CREATE TABLE MaintenanceLog (
    log_id        SERIAL PRIMARY KEY,
    equipment_id  INT REFERENCES Equipment(equipment_id) ON DELETE CASCADE,
    admin_id      INT REFERENCES AdministrativeStaff(admin_id) ON DELETE SET NULL,
    issue_reported TEXT,
    report_date   DATE DEFAULT CURRENT_DATE,
    resolve_date  DATE,
    log_status    VARCHAR(50)
);

--creating index
CREATE INDEX idx_member_email
ON Member(email);

--view for user profile
CREATE VIEW MemberDashboard AS
SELECT
    m.member_id,
    m.name AS member_name,
    m.email,

    --latest health metrics
    hm_latest.weight AS latest_weight,
    hm_latest.heartrate AS latest_heartrate,
    hm_latest.body_fat_percentage AS latest_bodyfat,

    --active goal
    fg_active.goal_type AS active_goal_type,
    fg_active.target_weight AS active_goal_target,
    fg_active.end_date AS goal_end_date,

    --past class count
    (
        SELECT COUNT(*)
        FROM ClassRegistration cr
        JOIN Class c ON cr.class_id = c.class_id
        WHERE cr.member_id = m.member_id
        AND c.date < CURRENT_DATE
    ) AS past_class_count,

    --next PT session date
    (
        SELECT MIN(ps.date)
        FROM PTSession ps
        WHERE ps.member_id = m.member_id
        AND ps.date >= CURRENT_DATE
    ) AS next_pt_session

FROM Member m

--left join --> latest metric per member
LEFT JOIN (
    SELECT DISTINCT ON (member_id) *
    FROM HealthMetrics
    ORDER BY member_id, recorded_at DESC
) hm_latest
ON m.member_id = hm_latest.member_id

--the active goal
LEFT JOIN (
    SELECT *
    FROM FitnessGoal
    WHERE status = 'active'
) fg_active
ON m.member_id = fg_active.member_id;

--create function for trigger
CREATE OR REPLACE FUNCTION prevent_trainer_overbooking()
RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM PTSession ps
        WHERE ps.trainer_id = NEW.trainer_id
          AND ps.date = NEW.date
          AND ps.session_id <> NEW.session_id
          AND (
                NEW.start_time < ps.end_time
                AND NEW.end_time > ps.start_time
              )
    ) THEN
        RAISE EXCEPTION
            'Trainer % is already booked on % during that time.',
            NEW.trainer_id, NEW.date;
    END IF;

    RETURN NEW;
END;
$$;

--view for class overview
 SELECT c.class_id,
    c.name AS class_name,
    c.date,
    c.start_time,
    c.end_time,
    c.capacity,
    t.name AS trainer_name,
    r.name AS room_name
   FROM class c
     LEFT JOIN trainer t ON c.trainer_id = t.trainer_id
     LEFT JOIN room r ON c.room_id = r.room_id
  ORDER BY c.date, c.start_time;

--member views what class they are in for the class registration
 SELECT cr.registration_id,
    cr.member_id,
    c.name AS class_name,
    c.date,
    c.start_time,
    c.end_time,
    t.name AS trainer_name,
    r.name AS room_name,
    cr.registered_date
   FROM classregistration cr
     JOIN class c ON cr.class_id = c.class_id
     JOIN trainer t ON c.trainer_id = t.trainer_id
     JOIN room r ON c.room_id = r.room_id;

--create trigger
CREATE TRIGGER trg_prevent_trainer_overbooking
BEFORE INSERT OR UPDATE ON PTSession
FOR EACH ROW
EXECUTE FUNCTION prevent_trainer_overbooking();

CREATE OR REPLACE FUNCTION public.prevent_room_overbooking()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM PTSession ps
        WHERE ps.room_id = NEW.room_id
          AND ps.date = NEW.date
          AND ps.session_id <> NEW.session_id   --ignore the current session
          AND (
                NEW.start_time < ps.end_time
                AND NEW.end_time > ps.start_time
              )
    ) THEN
        RAISE EXCEPTION
            'Room % is already booked on % during that time.',
            NEW.room_id, NEW.date;
    END IF;

    RETURN NEW;
END;
$function$
