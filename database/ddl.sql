CREATE TABLE events
(
    id CHAR(36) NOT NULL,
    type VARCHAR(250) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    aggregate_id CHAR(36) NOT NULL,
    metadata LONGTEXT,
    payload LONGTEXT
);
CREATE UNIQUE INDEX events_id_uindex ON events (id);
CREATE INDEX events_aggregate_id_index ON events (aggregate_id);