CREATE TABLE queue (
  id SERIAL PRIMARY KEY,
  created TIMESTAMP DEFAULT NOW(),
  creator INTEGER NOT NULL);
 
 CREATE TABLE message (
  id SERIAL PRIMARY KEY,
  sender INTEGER NOT NULL,
  receiver INTEGER,
  queue_id INTEGER NOT NULL,
  arrival TIMESTAMP DEFAULT NOW(),
  message TEXT NOT NULL,
  CONSTRAINT queue_id FOREIGN KEY (queue_id) REFERENCES queue (id));

CREATE INDEX idx_message_receiver_queue_id ON message(receiver, queue_id);
CREATE INDEX idx_message_queue_id_arrival ON message(queue_id, arrival);
CREATE INDEX idx_message_sender_arrival ON message(sender, arrival);
