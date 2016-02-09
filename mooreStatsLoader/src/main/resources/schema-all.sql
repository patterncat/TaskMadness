CREATE TABLE IF NOT EXISTS MOORE_NCAA_STATS  (
    YEAR INT NOT NULL,
    RANK INT NOT NULL,
    NAME VARCHAR(50) NOT NULL,
    WIN INT NOT NULL,
    LOSS INT NOT NULL,
    TIE INT NOT NULL,
    SOS DOUBLE NOT NULL,
    PR DOUBLE NOT NULL
);
