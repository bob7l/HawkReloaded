package uk.co.oliwali.HawkEye.blocks;

public class HawkBlockType {

	public static final TallPlant tallplant = new TallPlant();
	public static final Default Default = new Default();
	public static final TopBlock topblock = new TopBlock();
	public static final AttachedBlock attachedblock = new AttachedBlock();
	public static final BasicBlock basicblock = new BasicBlock();
	public static final VineBlock vine = new VineBlock();
	public static final BedBlock bed = new BedBlock();
	public static final DoorBlock door = new DoorBlock();
	public static final Plant plant = new Plant();
	public static final Container container = new Container();
	public static final SignBlock sign = new SignBlock();
	public static final PistonBlock piston = new PistonBlock();
	public static final LeafBlock leaf = new LeafBlock();
	public static final DoublePlant doubleplant = new DoublePlant();

	public static HawkBlock getHawkBlock(int i) {
		switch (i) {
		case 18:
			return leaf;
		case 54:
		case 146:
		case 61:
		case 62:
		case 23:
			return container;
		case 63:
		case 68:
			return sign;
		case 26:
			return bed;
		case 64:
		case 71:
			return door;
		case 59:
		case 104:
		case 105:
		case 141:
		case 142:
			return plant;
		case 81:
		case 83:
			return tallplant;
		case 50:
		case 65:
		case 66:
		case 69:
		case 75:
		case 76:
		case 77:
		case 96:
		case 127:
		case 131:
		case 44:
		case 157:
			return attachedblock;
		case 6:
		case 27:
		case 28:
		case 31:
		case 32:
		case 37:
		case 38:
		case 39:
		case 40:
		case 70:
		case 72:
		case 78:
		case 92:
		case 93:
		case 55:
		case 94:
		case 132:
		case 140:
		case 147:
		case 148:
		case 149:
		case 150:
		case 115:
		case 171:
		case 404:
		case 356:
			return topblock;
		case 20:
		case 25:
		case 30:
		case 46:
		case 51:
		case 53:
		case 58:
		case 79:
		case 84:
		case 89:
		case 101:
		case 102:
		case 107:
		case 108:
		case 109:
		case 111:
		case 113:
		case 114:
		case 116:
		case 117:
		case 118:
		case 119:
		case 120:
		case 122:
		case 126:
		case 128:
		case 130:
		case 134:
		case 135:
		case 136:
		case 137:
		case 138:
		case 144:
		case 151:
		case 154:
		case 156:
		case 158:
		case 397:
		case 380:
		case 379:
			return basicblock;
		case 106:
			return vine;
		case 29:
		case 33:
		case 34:
			return piston;
		case 175:
			return doubleplant;

		default: return Default;
		}
	}
}
