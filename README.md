# 🎌 Anime Suggestion Generator

A Java based desktop application that helps users discover anime based on genre,
title, or keyword. Built with Swing for the GUI and a terminal mode as well.

## Features
- User registration and login (stored locally)
- Search anime by title or genre
- Filter results by year, type, and origin
- Fetches live data from the [Jikan API](https://jikan.moe/) (MyAnimeList)
- Displays anime cards with cover images, episode count, and popularity

## How to Run
1. Make sure you have JDK 11 or higher installed
2. Compile all `.java` files in the `animesuggestiongenerator` package
3. Run `MainApp` for the GUI version
4. Run `MainAppTerminal` for the terminal version

## Tech Stack
- Java (Swing for GUI)
- Jikan REST API v4
- File-based user storage

## Project Structure
| File | Role |
|---|---|
| `AppGui.java` | GUI layout and screens |
| `AnimeEngine.java` | Search logic and result parsing |
| `WebConnector.java` | HTTP requests to the API |
| `FilterMaker.java` | Applies year, type, origin filters |
| `UserManager.java` | Login, signup, user file I/O |
| `TerminalApp.java` | Terminal interface |
