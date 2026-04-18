CREATE TABLE feedback_notes (
    id          UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    engineer_id UUID REFERENCES engineers(id) ON DELETE CASCADE,
    date        DATE NOT NULL,
    giver       VARCHAR(255),
    situation   TEXT,
    task        TEXT,
    action      TEXT,
    result      TEXT
);

CREATE INDEX idx_feedback_notes_engineer_id ON feedback_notes(engineer_id);
CREATE INDEX idx_feedback_notes_date ON feedback_notes(date DESC);
