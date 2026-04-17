CREATE TABLE notes (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    engineer_id UUID         NOT NULL REFERENCES engineers(id) ON DELETE CASCADE,
    date        DATE         NOT NULL,
    body        TEXT         NOT NULL
);

CREATE INDEX idx_notes_engineer_id ON notes(engineer_id);
