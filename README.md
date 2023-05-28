## Lichess Puzzles

### Architecture & work flow

- Downloader:
    - download puzzles from Lichess Database
    - Convert them into Puzzle case class
    - Process each puzzle as below:
        - if it already exists => update its data to database
        - if not, save it to database & put it to a kafka queue to get game information

- kafka consumer
    - Fetch game information from Lichess Api
    - Save into database

- Backend - API
    - provide API

## sql tables

### users

- id

### games

- id
- white
- black
- moves
- others

### puzzle

- id
- gameId
- fen
- moves
- rating
- ...
- white
- black

### themes

- id
- name

### Opening

- maybe not, just calculate from scalachess
