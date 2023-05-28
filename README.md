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
