CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TYPE mood AS ENUM ('white', 'black');

CREATE TABLE IF NOT EXISTS users
(
    id                 text PRIMARY KEY, -- lichess id
    name               text NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS game
(
    id                 text PRIMARY KEY,
    white_id           text NOT NULL,
    black_id           text NOT NULL,
    rated              boolean NOT NULL,
    variant            text NOT NULL,
    speed              text NOT NULL,
    perf               text NOT NULL,
    played_at          timestamptz NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW(),
    FOREIGN KEY (white_id) REFERENCES users,
    FOREIGN KEY (black_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS puzzle
(
    id                 text PRIMARY KEY,
    game_id            text UNIQUE,
    fen                text NOT NULL,
    moves              text NOT NULL,
    rating             integer NOT NULL,
    rating_deviation   integer NOT NULL,
    popularity         integer NOT NULL,
    play_times         integer NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW(),
    FOREIGN KEY (game_id) REFERENCES game
);

CREATE TABLE IF NOT EXISTS theme
(
    id                 serial PRIMARY KEY,
    name               text UNIQUE NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS puzzle_theme
(
    puzzle_id          text NOT NULL,
    theme_id           serial NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW(),
    FOREIGN KEY (puzzle_id) REFERENCES puzzle,
    FOREIGN KEY (theme_id) REFERENCES theme,
    PRIMARY KEY (puzzle_id, theme_id)
);

CREATE TABLE IF NOT EXISTS opening
(
    id                 text PRIMARY KEY,
    name               text UNIQUE NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS puzzle_opening
(
    puzzle_id          text NOT NULL,
    opening_id         text NOT NULL,
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW(),
    FOREIGN KEY (puzzle_id) REFERENCES puzzle,
    FOREIGN KEY (opening_id) REFERENCES opening,
    PRIMARY KEY (puzzle_id, opening_id)
);

CREATE TRIGGER set_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

CREATE TRIGGER set_game_updated_at
BEFORE UPDATE ON game
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

CREATE TRIGGER set_puzzle_updated_at
BEFORE UPDATE ON puzzle
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();
CREATE TRIGGER set_theme_updated_at
BEFORE UPDATE ON theme
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

CREATE TRIGGER opening_updated_at
BEFORE UPDATE ON opening
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

CREATE TRIGGER puzzle_theme_updated_at
BEFORE UPDATE ON puzzle_theme
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();

CREATE TRIGGER puzzle_opening_updated_at
BEFORE UPDATE ON puzzle_opening
FOR EACH ROW
EXECUTE PROCEDURE set_updated_at();
