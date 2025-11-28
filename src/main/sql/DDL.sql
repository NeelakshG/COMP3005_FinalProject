CREATE TABLE Member (
    member_id      SERIAL PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    phone          VARCHAR(20) UNIQUE,
    password       VARCHAR(100),
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

--create trigger
CREATE TRIGGER trg_prevent_trainer_overbooking
BEFORE INSERT OR UPDATE ON PTSession
FOR EACH ROW
EXECUTE FUNCTION prevent_trainer_overbooking();

