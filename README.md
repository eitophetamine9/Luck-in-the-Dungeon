# 🎲 Luck in the Dungeon 🏰

A Java-based gacha escape room game with time travel elements for our OOP Capstone Project.

## 👥 Team: Code Hub
**Members:**
1. Ray Christian C. Romales
2. Lance Benedict M. Arquillano
3. John Erick I. Santizo
4. Niño Michael M. Mahusay
5. Justine Kirby T. Lepon

## 🌟 Story Overview
**Luck in the Dungeon** combines escape room puzzles with gacha mechanics in a time-bending adventure. You've been trapped in a mysterious temporal dungeon by an evil chronomancer who has scattered a time machine across different laboratory rooms. Your luck and wits are the only tools to find all the components and rebuild the device to escape.

### 🎯 Game Objective:
Navigate through 4 temporal laboratory rooms, solve time-based puzzles, collect time machine components through gacha pulls, and rebuild the chronomancer's device to escape the dungeon.

## 🏗️ System Architecture

### Core Game Systems:
- **Laboratory Exploration**: Progressive room-based gameplay through temporal labs
- **Enhanced Puzzle Engine**: Time-based puzzles with progressive hint system
- **Luck-Based Gacha**: Collect time machine components with rarity tiers
- **Robust Save System**: Automatic backups with corruption protection
- **Map Navigation System**: Room progression system
- **Achievement Tracking**: Comprehensive player accomplishment system

### Technical Features:
- **Backup & Recovery**: Automatic save file backups with restoration capabilities
- **Game State Validation**: Prevents corruption and ensures data integrity
- **Enhanced Error Handling**: User-friendly messages instead of harsh exceptions
- **Progressive Difficulty**: Puzzles that adapt to player performance
- **Modular Expansion**: Ready for future content expansions

## 🎮 Gameplay Features

### Room Progression:
1. **Ruined Time Lab** - Discover basic time machine components in the destroyed laboratory
2. **Research Archives** - Decode the chronomancer's temporal research notes
3. **Chronal Alchemy Lab** - Create exotic materials needed for time travel
4. **Assembly Observatory** - Final room to assemble the time machine and escape

### Enhanced Mechanics:
- **Smart Item Usage**: Contextual feedback and helpful guidance
- **Time Machine Assembly**: Track collected components and assembly progress
- **Story Integration**: Every puzzle ties into the escape narrative
- **Achievement System**: Unlock badges for completing challenges
- **Progressive Hints**: Gets more helpful after multiple attempts

### Gacha System:
- **Working Pity System**: Guaranteed epic item every 10 pulls
- **Balanced Rates**: COMMON (60%) → RARE (30%) → EPIC (10%)
- **Meaningful Items**: All components contribute to time machine assembly
- **Smart Inventory**: Capacity warnings and organizational tools

## 🚀 Quick Start

### Prerequisites
- Java JDK 17 or higher

### Running the Game
```bash
# If we included a JAR file:
java -jar LuckInTheDungeon.jar

# From source code:
javac -d bin src/*.java src/**/*.java
java -cp bin main.Main
