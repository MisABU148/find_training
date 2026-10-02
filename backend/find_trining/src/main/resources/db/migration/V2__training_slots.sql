CREATE TABLE training_slots
(
    id         BIGSERIAL PRIMARY KEY,
    gym_id     BIGINT    NOT NULL REFERENCES gyms (id) ON DELETE CASCADE,
    coach_id   BIGINT    NOT NULL REFERENCES coaches (id) ON DELETE CASCADE,
    starts_at  TIMESTAMP NOT NULL,
    booked_by  BIGINT REFERENCES users (id) ON DELETE SET NULL,
    booked_at  TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    -- один и тот же зал не может быть занят на один и тот же час дважды
    CONSTRAINT uq_training_slots_gym_time UNIQUE (gym_id, starts_at),
    -- один и тот же тренер не может вести два занятия одновременно (в разных залах)
    CONSTRAINT uq_training_slots_coach_time UNIQUE (coach_id, starts_at)
);

-- один и тот же пользователь не может быть одновременно записан на два занятия
-- (частичный индекс: не мешает нескольким свободным слотам с booked_by = NULL)
CREATE UNIQUE INDEX uq_training_slots_user_time
    ON training_slots (booked_by, starts_at)
    WHERE booked_by IS NOT NULL;

CREATE INDEX idx_training_slots_gym_starts_at ON training_slots (gym_id, starts_at);
CREATE INDEX idx_training_slots_booked_by ON training_slots (booked_by);
