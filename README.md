# Luck in the Dungeon 🎮

A Java-based gacha escape room game for our OOP Capstone Project.

## 👥 Team: Code Hub
**Members:**
1. Ray Christian C. Romales
2. Lance Benedict M. Arquillano
3. John Erick I. Santizo
4. Niño Michael M. Mahusay
5. Justine Kirby T. Lepon

## 🏗️ System Overview
**Luck in the Dungeon** is an object-oriented Java application that combines escape room puzzle mechanics with gacha game elements. The system is built using core OOP principles and follows a modular architecture.

### Core System Components:
- **Room Management System**: Handles 4 progressive rooms with increasing difficulty
- **Puzzle Engine**: Manages different puzzle types (locks, codes, riddles) with polymorphic solving
- **Gacha Mechanics**: Randomized item acquisition with rarity tiers and probability management
- **Inventory System**: Item storage and usage management
- **Save/Load System**: JSON-based persistence for game state
- **GUI Interface**: Swing-based user interface for game interaction

### Technical Architecture:
The system implements a **layered architecture** with clear separation between:
- **Model Layer**: Game state, player data, puzzle logic
- **View Layer**: Swing GUI components
- **Controller Layer**: Game flow and user input handling

## 🎮 Gameplay Overview
**How to Play:**
1. **Solve Puzzles** in each room to earn coins
2. **Use Coins** to pull from gacha machines
3. **Collect Items** of different rarities (Common, Rare, Epic)
4. **Use Items** to solve specific puzzles
5. **Progress** through 4 rooms of increasing difficulty
6. **Escape** the dungeon to win!

## 🚀 Quick Start

### Prerequisites
- Java JDK 17 or higher

### Running the Game
```bash
# If we included a JAR file:
java -jar LuckInTheDungeon.jar

# From source code:
javac -d bin src/*.java src/**/*.java
java -cp bin Main

