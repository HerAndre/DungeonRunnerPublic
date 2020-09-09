package player;

import dungeon.Dungeon;
import dungeon.DungeonMap;
import dungeon.DungeonMonsters;
import monsters.Monster;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Interactions {

    //This file gets clunky

    private final Player player;
    private Monster monsterInPlayerRoom;

    public Interactions(Player player) {
        this.player = player;
    }

    //Checks if player is in a room with a monster and saves the monster in monsterInPlayerRoom var for later access.
    public boolean withinMonsterRoom(DungeonMonsters dungeonMonsters) {
        for (Object object : dungeonMonsters.getMonsterList()) {
            Monster monster = (Monster) object;
            if (player.location().getLocation().equals(monster.getLocation()) && (monster.getHitpoints() > 0)) { //if monster is alive and in the room
                this.monsterInPlayerRoom = monster;
                return true;
            }
        }
        return false;
    }

    //returns if the player is alive
    public boolean playerAlive() {
        if (player.skills().getHitpoints() > 0) {
            return true;
        }
        System.out.println("\nYou died!");
        return false;
    }

    final Scanner scannerBattle = new Scanner(System.in);
    private boolean notifyPlayer = true;


    //handles the fight between player and monster
    public void playerBattle() {
        if (playerAlive() && monsterInPlayerRoom.getHitpoints() > 0) {
            if (notifyPlayer) {
            System.out.println("You spot a " + monsterInPlayerRoom.getName() + " in the room. You attack it quickly.");
            notifyPlayer = false;
            }
            scannerBattle.nextLine();
            monsterInPlayerRoom.monsterTakeDamage(monsterInPlayerRoom.rollDamage(player.skills().getStrength(),player.skills().getAccuracy()));
            scannerBattle.nextLine();
        }
       if (monsterInPlayerRoom.getHitpoints() > 0) { // make sure monster isnt dead so it can attack
           int monsterDamage = monsterInPlayerRoom.rollDamage(monsterInPlayerRoom.getStrength(), monsterInPlayerRoom.getAccuracy());
           monsterInPlayerRoom.monsterInflictDamage(monsterDamage, player);
       } else {
           notifyPlayer = true;
           System.out.println("You defeated the monster!\nPress enter to continue...");
           scannerBattle.nextLine();
           //probably add loot drop here? add loot to monsterInPlayerRoom object
           //easy way to add point system
           //grab player points and chuck points onto it here.
       }
    }

    //Runs through the predefined rooms in dungeon class and sees if any are nearby the player coordinate (+/- 1 on each axis).
    //The formulas could use some simplification and helper functions to make it less ugly
    public void availableLocations(Dungeon dungeon, ArrayList<String> possibleLocations) {
        if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX(),
                (int) player.location().getLocation().getY() + 1 ))) {
            possibleLocations.add(dungeon.getDungeonLayout().get(new Point(player.location().getLocation().x, player.location().getLocation().y + 1)) + " (North)");
        }
        if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX() + 1,
                (int) player.location().getLocation().getY() ))) {
            possibleLocations.add(dungeon.getDungeonLayout().get(new Point(player.location().getLocation().x + 1, player.location().getLocation().y)) + " (East)");
        }

        if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX(),
                (int) player.location().getLocation().getY() -1 ))) {
            possibleLocations.add(dungeon.getDungeonLayout().get(new Point(player.location().getLocation().x, player.location().getLocation().y -1)) + " (South)");
        }

        if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX() -1 ,
                (int) player.location().getLocation().getY() ))) {
            possibleLocations.add(dungeon.getDungeonLayout().get(new Point(player.location().getLocation().x -1, player.location().getLocation().y )) + " (West)");
        }
        System.out.println("Available Options: " + possibleLocations );
        possibleLocations.clear();
    }


    //handles everything with player movement (map, user input n,e,s,w)
    final Scanner scanner = new Scanner(System.in);

    public void playerMovement(Dungeon dungeon, DungeonMap dungeonMap) {
        String playerInput = scanner.nextLine();

        if (playerInput.equalsIgnoreCase("n")) {
            if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX(), // Checks if the direction the player chose is a valid room in the hashmap
                    (int) player.location().getLocation().getY() + 1 ))) {
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y ) * DungeonMap.WIDTH, '0'); // Mark last location before we move
                player.location().getLocation().setLocation(player.location().getLocation().getX(), player.location().getLocation().getY() + 1); // Moves the player north or 1 point up
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y ) * DungeonMap.WIDTH, 'X'); // Mark new location

            } else {    // There was no room with the matching coordinates found in the hashmap
                System.out.println("There doesn't seem to be a path this way." +
                        "\nPress enter to continue... ");
                scanner.nextLine();
            }
        } else if (playerInput.equalsIgnoreCase("e")) {
            if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX() + 1,
                    (int) player.location().getLocation().getY() ))) {
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y ) * DungeonMap.WIDTH, '0');
                player.location().getLocation().setLocation(player.location().getLocation().getX() + 1, player.location().getLocation().getY());
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y) * DungeonMap.WIDTH, 'X');

            } else {
                System.out.println("There doesn't seem to be a path this way." +
                        "\nPress enter to continue... ");
                scanner.nextLine();
            }
        } else if (playerInput.equalsIgnoreCase("s")) {
            if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX(),
                    (int) player.location().getLocation().getY() -1 ))) {
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y ) * DungeonMap.WIDTH, '0');
                player.location().getLocation().setLocation(player.location().getLocation().getX(), player.location().getLocation().getY() -1 );
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y) * DungeonMap.WIDTH, 'X');

            } else {
                System.out.println("There doesn't seem to be a path this way." +
                        "\nPress enter to continue... ");
                scanner.nextLine();
            }
        } else if (playerInput.equalsIgnoreCase("w")) {
            if (dungeon.getDungeonLayout().containsKey(new Point( (int) player.location().getLocation().getX() -1,
                    (int) player.location().getLocation().getY() ))) {
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y ) * DungeonMap.WIDTH, '0');
                player.location().getLocation().setLocation(player.location().getLocation().getX() -1, player.location().getLocation().getY() );
                dungeonMap.getMapUnits().set((player.location().getLocation().x -1 ) + (DungeonMap.HEIGHT - player.location().getLocation().y) * DungeonMap.WIDTH, 'X');

            } else {
                System.out.println("There doesn't seem to be a path this way." +
                        "\nPress enter to continue... ");
                scanner.nextLine();
            }
        }
    }
}
