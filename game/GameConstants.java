package tactics;

public class GameConstants {
	
	public static String baseResPath = "tactics/res/";
	public static String baseImgPath = "tactics/res/img/";
	public static String boardImgPath = baseImgPath + "board/wargame_board_3_mod.png";
	public static String outlineImgPath = baseImgPath + "cursquare_outline.png";
	public static String miniMapImgPath = baseImgPath + "board/wargame_board_minimap.png";
	public static String combatOddsImgPath = baseImgPath + "combat_odds.png";
	public static String[][] unitImgPath = {
		{
			baseImgPath + "units/red/red_infantry.jpg",
			baseImgPath + "units/red/red_armor.jpg",
			baseImgPath + "units/red/red_airborne.jpg",
			baseImgPath + "units/red/red_amphib.jpg",
			baseImgPath + "units/red/red_mountain.jpg",
			baseImgPath + "units/red/red_corps_HQ.jpg",
			baseImgPath + "units/red/red_army_HQ.jpg",
			baseImgPath + "units/red/red_armygroup_HQ.jpg"
		},
		{
			baseImgPath + "units/blue/blue_infantry.jpg",
			baseImgPath + "units/blue/blue_armor.jpg",
			baseImgPath + "units/blue/blue_airborne.jpg",
			baseImgPath + "units/blue/blue_amphib.jpg",
			baseImgPath + "units/blue/blue_mountain.jpg",
			baseImgPath + "units/blue/blue_corps_HQ.jpg",
			baseImgPath + "units/blue/blue_army_HQ.jpg",
			baseImgPath + "units/blue/blue_armygroup_HQ.jpg"
		}
	};

	public static String mapDataPath = baseResPath + "data/mapdata.txt";
	public static String roadDataPath = baseResPath + "data/roaddata.txt";
	public static String riverDataPath = baseResPath + "data/riverdata.txt";
	public static String unitDataPath = baseResPath + "data/unitdata.txt";

	public static String[][] keyMaps = {
		{"NW", "I"}, {"N", "O"}, {"NE", "P"},
		{"E", "SEMICOLON"}, {"SE", "SLASH"}, {"S", "PERIOD"},
		{"SW", "COMMA"}, {"W", "K"}
	};
	
	public static int LARGE_SQUARE_SIZE = 50, SMALL_SQUARE_SIZE = 25;
	public static int LARGE_UNIT_OFFSET = 3, SMALL_UNIT_OFFSET = 1;

	public static int N_COLS = 44, N_ROWS = 55;

	public static int NUM_UNITS = 88,
					  NUM_RED_UNITS = 44, NUM_BLUE_UNITS = 44;

	public static int NUM_ARMIES = 2;
	public static int RED_ARMY = 0, BLUE_ARMY = 1;
	public static String[] armyNames = { "Red Army", "Blue Army" };

	public static int NUM_UNIT_TYPES = 8;
	public static int INFANTRY = 0, ARMOR = 1, AIRBORNE = 2, 
					  AMPHIB = 3, MOUNTAIN = 4, CORPS_HQ = 5,
					  ARMY_HQ = 6, ARMYGROUP_HQ = 7;
	public static String[] unitNames = { "Infantry", "Armor", "Airborne",
										 "Amphib", "Mountain", "Corps HQ",
										 "Army HQ", "Army Group HQ" };

	public static int NUM_TERRAIN_TYPES = 6;
	public static int OCEAN_TILE = 0, PLAINS_TILE = 1,
					  FOREST_TILE = 2, MOUNTAIN_TILE = 3,
					  CITY_TILE = 4, BEACH_TILE = 5;
	public static String[] terrainNames = { "Ocean", "Open",
											"Forest", "Mountains",
											"City", "Beach" };

	public static int RED_MOVE = 0, RED_COMBAT = 1, RED_RESOLVE = 2,
					  BLUE_MOVE = 3, BLUE_COMBAT = 4, BLUE_RESOLVE = 5,
					  GAME_OVER = 6;
	public static String[] gameStateNames = { "Red's move", "Red in combat", "Red resolving combat",
											  "Blue's move", "Blue in combat", "Blue resolving combat",
											  "Game over" };


}