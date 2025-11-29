package model;

public class ToolItem extends GachaItem {
    private String toolType;
    private int usesRemaining;

    public ToolItem(String name, String description, Rarity rarity,
                    String toolType, int usesRemaining){
        super(name,description,rarity, ItemType.TOOL);
        this.toolType = toolType;
        this.usesRemaining = usesRemaining;
    }

    @Override
    public boolean use(Puzzle puzzle){
        if(usesRemaining <= 0) {
            System.out.println("This tool is broken and cannot be used!");
            return false;
        }

        boolean wasUsed = false;

        if(puzzle instanceof LockPuzzle && "lockpick".equals(toolType)){
            LockPuzzle lock = (LockPuzzle) puzzle;
            System.out.println("🔓 Lockpick reveals: This lock requires a " +
                    lock.getLockColor() + " key");
            wasUsed = true;

        } else if(puzzle instanceof CodePuzzle && "decoder".equals(toolType)){
            CodePuzzle codePuzzle = (CodePuzzle) puzzle;
            String description = codePuzzle.getDescription();

            // UPDATED: Handle new story puzzles with BETTER HINTS
            if (description.contains("time sequence") || description.contains("12:00")) {
                String timeHint = guideTimeSequence(description);
                System.out.println("⏰ Temporal Analyzer: " + timeHint);
            }
            else if (description.contains("Scientist's code") || description.contains("T = 20")) {
                String codeHint = guideLetterCode(description);
                System.out.println("🔢 Cryptography Module: " + codeHint);
            }
            else if (description.contains("Mix elements") || description.contains("Temporal Energy")) {
                String formulaHint = guideEnergyFormula(description);
                System.out.println("⚡ Alchemy Calculator: " + formulaHint);
            }
            else if (description.contains("activation code") || description.contains("year")) {
                String yearHint = guideYearActivation(description);
                System.out.println("📅 Chronal Database: " + yearHint);
            }
            else {
                String genericHint = guideGenericPuzzle(description);
                System.out.println("🔍 Logic Processor: " + genericHint);
            }
            wasUsed = true;

        } else if (puzzle instanceof RiddlePuzzle && "hintbook".equals(toolType)) {
            RiddlePuzzle riddle = (RiddlePuzzle) puzzle;
            System.out.println("📖 Temporal Encyclopedia: " + getRiddleGuidance(riddle));
            wasUsed = true;
        }

        // Time component tools
        else if("time_component".equals(toolType)) {
            System.out.println("💎 The " + name + " hums with temporal energy...");
            if (name.contains("Crystal")) {
                System.out.println("✨ This crystal can focus temporal energy for precise time jumps!");
            } else if (name.contains("Circuit")) {
                System.out.println("🔌 These circuits route temporal energy through the machine!");
            } else if (name.contains("Flux Capacitor")) {
                System.out.println("⚡ This is the core component that makes time travel possible!");
            } else if (name.contains("Stabilizer")) {
                System.out.println("🛡️ This prevents temporal paradoxes during time travel!");
            } else {
                System.out.println("🚀 This appears to be part of a time machine!");
            }
            wasUsed = true;
        }

        // Blueprint tools
        else if("blueprint".equals(toolType)) {
            System.out.println("📜 Studying the " + name + "...");
            System.out.println("🔧 These schematics reveal: " + getBlueprintInsight(name));
            wasUsed = true;
        }

        if (wasUsed) {
            usesRemaining--;
            if (usesRemaining <= 0) {
                System.out.println("❌ This " + toolType + " has been fully utilized!");
            }
        } else {
            System.out.println("⚠️ This " + toolType + " cannot help with this type of puzzle.");
        }

        return wasUsed;
    }

    // UPDATED HELPER METHODS WITH ACTUALLY USEFUL HINTS:

    private String guideTimeSequence(String description) {
        return "Time patterns often follow consistent intervals. Look at the difference between each time: " +
                "12:00 to 12:15 = 15 minutes, 12:15 to 12:30 = 15 minutes. " +
                "The pattern continues with the same 15-minute increment.";
    }

    private String guideLetterCode(String description) {
        return "The scientist assigned numerical values to letters. " +
                "To find TIME, calculate: T(20) + I(7) + M(15) + E(25). " +
                "Add these four numbers together to get the total.";
    }

    private String guideEnergyFormula(String description) {
        return "When mixing energy sources in alchemy, you simply combine the quantities. " +
                "If you have 2 units of one energy and 3 units of another, " +
                "the total is the sum of both amounts.";
    }

    private String guideYearActivation(String description) {
        return "Time machines often use significant dates as activation codes. " +
                "Consider when major temporal events occurred in the story. " +
                "The scientist likely traveled back to a year that holds importance " +
                "in the current timeline.";
    }

    private String guideGenericPuzzle(String description) {
        if (description.contains("binary")) {
            return "Binary codes use 1s and 0s. Each group of 8 bits represents one character " +
                    "using the ASCII standard. Convert each binary group to decimal, " +
                    "then find the corresponding letter.";
        }
        return "Look for mathematical operations, sequences, or patterns. " +
                "Break the problem down into smaller steps.";
    }

    private String getRiddleGuidance(RiddlePuzzle riddle) {
        String question = riddle.getQuestion();

        // Room 1: Clock riddle
        if (question.contains("hands but cannot clap") || question.contains("face but cannot see")) {
            return "This describes an object with moving parts (hands) and a display (face) " +
                    "that measures time. Common examples include watches and clocks. " +
                    "The answer is a time-keeping device found in most rooms.";
        }

        // Room 2: Timeline reconstruction
        else if (question.contains("Machine Built") || question.contains("Reality Split")) {
            return "The sequence shows the problem: the scientist built a machine, " +
                    "created an alternate reality, took power, then destroyed the machine. " +
                    "The logical next step is to reverse the damage by rebuilding what was lost.";
        }

        // Room 3: Material properties
        else if (question.contains("needed for time travel") || question.contains("glow but I'm not light")) {
            return "This material is essential for focusing energy in advanced technology. " +
                    "It's solid, often crystalline, and can luminesce. " +
                    "Think about what components are used in lasers or energy focusing devices.";
        }

        // Room 4: Time machine assembly
        else if (question.contains("Energy to power it") || question.contains("Crystal to focus it")) {
            return "Time travel exposes travelers to extreme temporal forces. " +
                    "You need something that maintains your existence across time jumps. " +
                    "The missing component ensures you remain intact during temporal transit.";
        }

        // Fallback with actual guidance
        return "Analyze each part of the riddle separately. Identify the key characteristics " +
                "mentioned and think of objects that match all those descriptions.";
    }

    private String getBlueprintInsight(String blueprintName) {
        if (blueprintName.contains("Schematic")) {
            return "Time machines require three main systems: power generation, " +
                    "temporal focusing, and reality anchoring. These schematics show " +
                    "how to connect the energy core to the flux capacitor.";
        }
        return "Proper assembly sequence: power source first, then stabilizers, " +
                "finally the navigation system. Never activate without all safety systems in place.";
    }

    public String getToolType(){return toolType;}
    public int getUsesRemaining(){return usesRemaining;}
}