CREATE OR REPLACE FUNCTION send_message(
    _sender INT, _queue_id INT, _message TEXT) RETURNS TABLE(arrival TIMESTAMP) AS $$
    INSERT INTO message(sender, receiver, queue_id, arrival, message)
    VALUES(_sender, null, _queue_id, DEFAULT, _message)
    RETURNING arrival;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION send_message(
    _sender INT, _receiver INT, _queue_id INT, _message TEXT) RETURNS TABLE(arrival TIMESTAMP) AS $$
    INSERT INTO message(sender, receiver, queue_id, arrival, message)
    VALUES(_sender, _receiver, _queue_id, DEFAULT, _message)
    RETURNING arrival;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION pop_message(_queue_id INT, _receiver INT) RETURNS TABLE(id INT, sender INT, text TEXT) AS $$
    DELETE FROM message AS m1
    WHERE m1.id = (
        SELECT m2.id
        FROM message AS m2
        WHERE queue_id = _queue_id AND (receiver IS NULL OR receiver = _receiver)
        ORDER BY queue_id, arrival
        LIMIT 1 FOR UPDATE
    ) RETURNING m1.id, m1.sender, m1.message;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION peek_message(_queue_id INT, _receiver INT) RETURNS TABLE(id INT, sender INT, text TEXT) AS $$
    SELECT id, sender, message
    FROM message
    WHERE queue_id = _queue_id AND (receiver IS NULL OR receiver = _receiver)
    ORDER BY queue_id, arrival
    LIMIT 1;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION peek_message_from_sender(_sender INT, _receiver INT) RETURNS TABLE(id INT, sender INT, text TEXT) AS $$
    SELECT id, sender, message
    FROM message
    WHERE sender = _sender AND (receiver IS NULL OR receiver = _receiver)
    ORDER BY sender, arrival
    LIMIT 1;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION query_queues(_receiver INT) RETURNS TABLE(id INT) AS $$
    SELECT DISTINCT ON (queue_id) queue_id
    FROM message
    WHERE receiver = _receiver
    ORDER BY queue_id;
$$ LANGUAGE sql IMMUTABLE;
