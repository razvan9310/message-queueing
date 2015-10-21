DROP FUNCTION send_message(_sender INT, _queue_id INT, _message TEXT);
DROP FUNCTION send_message(_sender INT, _receiver INT, _queue_id INT, _message TEXT);
DROP FUNCTION pop_message(_id INT, _sender INT);
DROP FUNCTION peek_message(_queue_id INT, _receiver INT);
DROP FUNCTION peek_message_from_sender(integer,integer);
DROP FUNCTION query_queues(_receiver INT);
