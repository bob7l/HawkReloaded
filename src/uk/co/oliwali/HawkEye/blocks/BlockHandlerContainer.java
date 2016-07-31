package uk.co.oliwali.HawkEye.blocks;

import uk.co.oliwali.HawkEye.blocks.blockhandlers.*;

public class BlockHandlerContainer {

    private final BlockHandler[] blockTypes;

    public BlockHandlerContainer() {

        TallPlantHandler tallplant = new TallPlantHandler(this);
        DefaultBlockHandler default_ = new DefaultBlockHandler(this);
        TopBlockHandler topblock = new TopBlockHandler();
        AttachedBlockHandler attachedblock = new AttachedBlockHandler();
        BasicBlockHandler basicblock = new BasicBlockHandler();
        VineBlockHandler vine = new VineBlockHandler();
        BedBlockHandler bed = new BedBlockHandler();
        DoorBlockHandler door = new DoorBlockHandler();
        PlantHandler plant = new PlantHandler();
        ContainerBlockHandler container = new ContainerBlockHandler();
        SignBlockHandler sign = new SignBlockHandler(this);
        PistonBlockHandler piston = new PistonBlockHandler();
        LeafBlockHandler leaf = new LeafBlockHandler(this);
        DoublePlantHandler doubleplant = new DoublePlantHandler();

        blockTypes = new BlockHandler[255]; //255 is the max block-id

        blockTypes[0] = new AirHandler();

        blockTypes[18] = leaf;

        blockTypes[54] = container;
        blockTypes[146] = container;
        blockTypes[61] = container;
        blockTypes[62] = container;
        blockTypes[23] = container;
        blockTypes[158] = container;
        blockTypes[154] = container;

        blockTypes[63] = sign;
        blockTypes[68] = sign;

        blockTypes[26] = bed;

        blockTypes[64] = door;
        blockTypes[71] = door;

        blockTypes[59] = plant;
        blockTypes[104] = plant;
        blockTypes[105] = plant;
        blockTypes[141] = plant;
        blockTypes[142] = plant;

        blockTypes[81] = tallplant;
        blockTypes[83] = tallplant;

        blockTypes[50] = attachedblock;
        blockTypes[65] = attachedblock;
        blockTypes[66] = attachedblock;
        blockTypes[69] = attachedblock;
        blockTypes[75] = attachedblock;
        blockTypes[76] = attachedblock;
        blockTypes[77] = attachedblock;
        blockTypes[96] = attachedblock;
        blockTypes[127] = attachedblock;
        blockTypes[131] = attachedblock;
        blockTypes[44] = attachedblock;
        blockTypes[157] = attachedblock;

        blockTypes[6] = topblock;
        blockTypes[27] = topblock;
        blockTypes[28] = topblock;
        blockTypes[31] = topblock;
        blockTypes[32] = topblock;
        blockTypes[37] = topblock;
        blockTypes[38] = topblock;
        blockTypes[39] = topblock;
        blockTypes[40] = topblock;
        blockTypes[70] = topblock;
        blockTypes[72] = topblock;
        blockTypes[78] = topblock;
        blockTypes[92] = topblock;
        blockTypes[93] = topblock;
        blockTypes[55] = topblock;
        blockTypes[94] = topblock;
        blockTypes[132] = topblock;
        blockTypes[140] = topblock;
        blockTypes[147] = topblock;
        blockTypes[148] = topblock;
        blockTypes[149] = topblock;
        blockTypes[150] = topblock;
        blockTypes[115] = topblock;
        blockTypes[171] = topblock;

        blockTypes[20] = basicblock;
        blockTypes[25] = basicblock;
        blockTypes[30] = basicblock;
        blockTypes[46] = basicblock;
        blockTypes[51] = basicblock;
        blockTypes[53] = basicblock;
        blockTypes[58] = basicblock;
        blockTypes[79] = basicblock;
        blockTypes[84] = basicblock;
        blockTypes[89] = basicblock;
        blockTypes[101] = basicblock;
        blockTypes[102] = basicblock;
        blockTypes[107] = basicblock;
        blockTypes[108] = basicblock;
        blockTypes[109] = basicblock;
        blockTypes[111] = basicblock;
        blockTypes[113] = basicblock;
        blockTypes[114] = basicblock;
        blockTypes[116] = basicblock;
        blockTypes[117] = basicblock;
        blockTypes[118] = basicblock;
        blockTypes[119] = basicblock;
        blockTypes[120] = basicblock;
        blockTypes[122] = basicblock;
        blockTypes[126] = basicblock;
        blockTypes[128] = basicblock;
        blockTypes[130] = basicblock;
        blockTypes[134] = basicblock;
        blockTypes[135] = basicblock;
        blockTypes[136] = basicblock;
        blockTypes[137] = basicblock;
        blockTypes[138] = basicblock;
        blockTypes[144] = basicblock;
        blockTypes[151] = basicblock;
        blockTypes[156] = basicblock;

        blockTypes[106] = vine;

        blockTypes[29] = piston;
        blockTypes[33] = piston;
        blockTypes[34] = piston;

        blockTypes[175] = doubleplant;

        //Replace all non-set block-id's to default
        for (int i = 1; i < blockTypes.length; i++)
            if (blockTypes[i] == null)
                blockTypes[i] = default_;

    }

    public BlockHandler getBlockHandler(int id) {
        return blockTypes[id];
    }

}
