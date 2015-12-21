SeledkaScene : AbstractScene {
    var <>swing;
    var routine_part1, routine_part2, routine_part3;

    *new {
        arg oscPort = 7000;
        ^super.new("Seledka", "/seledka", oscPort).initSeledka;
    }

    initSeledka {
        swing = 0.01;
        // arg name, path, port;
        // super.init(name, path, port);
    }
}

