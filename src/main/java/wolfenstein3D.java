import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import javafx.util.Pair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class wolfenstein3D {


    public static void main(String[] args) {
        System.out.println("Wolfenstein 3D");

        // Defining the screen size
        int screenWidth = 120;
        int screenHeight = 40;

        // Player position
        float playerX = 8.0f;
        float playerY = 8.0f;
        // Angle Position
        float playerA = 0.0f;

        // Defining the map
        int mapHeight = 16;
        int mapWidth = 16;


        // Distances read

        // Field of view determines how wide the player sees the world in front of him/her
        float fFOV = (float) (3.14159 / 4.0);

        float fDepth = 16.0f;

        String map = "";
        map += "########........";
        map += "#..............#";
        map += "#.......########";
        map += "#..............#";
        map += "#......##......#";
        map += "#......##......#";
        map += "#..............#";
        map += "###............#";
        map += "##.............#";
        map += "#......####..###";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#......#########";
        map += "#..............#";
        map += "################";


        Date tp1 = new Date(System.currentTimeMillis());
        Date tp2 = new Date(System.currentTimeMillis());


        Terminal terminal = null;
        try {

            terminal = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(screenWidth, screenHeight)).createTerminal();
            KeyStroke keyStroke = terminal.readInput();
            TextGraphics textGraphics = terminal.newTextGraphics();


            while (keyStroke.getKeyType() != KeyType.Escape) {


                SimpleDateFormat xs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
                tp2 = new Date(System.currentTimeMillis());

                double diffInMillies = (double) (tp2.getTime() - tp1.getTime());
                float diff = (float) diffInMillies;
                float elapsedTime = diff / (float) 1000.0;
                tp1 = tp2;


                if (keyStroke.getKeyType().name() == "Character") {

                    // Movement
                    // Left, A key
                    if (keyStroke.getCharacter().toString().toLowerCase().equals("a")) {
                        playerA -= (0.8f);
                    }
                    // Right, D key
                    if (keyStroke.getCharacter().toString().toLowerCase().equals("d")) {
                        playerA += (0.8f);
                    }
                    // Foward, W keyww
                    if (keyStroke.getCharacter().toString().toLowerCase().equals("w")) {

                        playerX += Math.sin(playerA);
                        playerY += Math.cos(playerA);


                        if (map.charAt(Math.abs((int) playerY * mapWidth + (int) playerX)) == '#') { // TODO REVISAR, aca tire terrible corte.
                            playerX -= Math.sin(playerA);
                            playerY -= Math.cos(playerA);
                        }
                    }
                    // Backward, S key
                    if (keyStroke.getCharacter().toString().toLowerCase().equals("s")) {

                        playerX -= Math.sin(playerA);
                        playerY -= Math.cos(playerA);


                        if (map.charAt(Math.abs((int) playerY * mapWidth + (int) playerX)) == '#') {
                            System.out.println("CHAR");
                            playerX += Math.sin(playerA);
                            playerY += Math.cos(playerA);
                        }
                    }

                    // Ray angle
                    for (int x = 0; x < screenWidth; x++) {
                        float fRayAngle = (playerA - fFOV / 2.0f) + ((float) x / (float) screenWidth) + fFOV;

                        float fDistanceToWall = 0;
                        Boolean bHitWall = false;
                        Boolean bBoundary = false;

                        // Unit vector for ray in player space
                        float fEyeX = (float) Math.sin(fRayAngle);
                        float fEyeY = (float) Math.cos(fRayAngle);


                        // Cutting distance micro step by micro step
                        while (!bHitWall && fDistanceToWall < fDepth) {
                            fDistanceToWall += 0.1f;

                            int nTestX = (int) (playerX + fEyeX * fDistanceToWall);
                            int nTestY = (int) (playerY + fEyeY * fDistanceToWall);

                            // Test if ray is out of bounds
                            if (nTestX < 0 || nTestX >= mapWidth || nTestY < 0 || nTestY >= mapHeight) {
                                bHitWall = true; // Maximun depth
                                fDistanceToWall = fDepth;
                            } else {

                                // Ray is inbounds so test to see if the ray cell is a wall block
                                if (map.charAt(nTestY * mapWidth + nTestX) == '#') {
                                    bHitWall = true;


                                    // Creating a list
                                    List<Pair<Float, Float>> l1 = new ArrayList<Pair<Float, Float>>();

                                    for (int tx = 0; tx < 2; tx++) {
                                        for (int ty = 0; ty < 2; ty++) {
                                            float vy = (float) nTestY + ty - playerY;
                                            float vx = (float) nTestX + tx - playerX;
                                            float d = (float) Math.sqrt(vx * vx + vy * vy);
                                            float dot = (fEyeX * vx / d) + (fEyeY * vy / d);


                                            Pair<Float, Float> pair = new Pair<Float, Float>(d, dot);
                                            l1.add(pair);
                                        }

                                        // Sort pairs from the closest one to the farthest one.
                                        l1.sort(new Comparator<Pair<Float, Float>>() {
                                            public int compare(Pair<Float, Float> o1, Pair<Float, Float> o2) {


                                                int rst;
                                                if (o1.getValue() == o2.getValue()) {
                                                    rst = 0;
                                                } else if (o1.getValue() < o2.getValue()) {
                                                    rst = -1;
                                                } else {
                                                    rst = -1;
                                                }

                                                return rst;
                                            }
                                        });

                                        float fBound = (float) 0.01;
                                        if (Math.acos(l1.get(0).getValue()) < fBound) bBoundary = true;
                                        if (Math.acos(l1.get(1).getValue()) < fBound) bBoundary = true;
                                    }
                                }
                            }


                        }

                        // Calculate distance to ceiling and floor
                        int nCeiling = (int) ((float) (screenHeight / 2.0) - screenHeight / ((float) fDistanceToWall));
                        int nFloor = screenHeight - nCeiling;


                        // Calculating the wall shades
                        short nShade = ' ';
                        if (fDistanceToWall <= fDepth / 4.0f)
                            nShade = 0x2588;
                        else if (fDistanceToWall < fDepth / 3.0f)
                            nShade = 0x2593;
                        else if (fDistanceToWall < fDepth / 2.0f)
                            nShade = 0x2592;
                        else if (fDistanceToWall < fDepth)
                            nShade = 0x2591;
                        else nShade = ' ';

                        if (bBoundary) nShade = ' ';

                        for (int y = 0; y < screenHeight; y++) {
                            if (y <= nCeiling) {
                                textGraphics.putString(x, y, " ");


                            } else if (y > nCeiling && y <= nFloor) {

                                textGraphics.putString(x, y, Character.toString((char) nShade));


                            } else {
                                // Shade floor based on distance
                                float b = 1.0f - (((float) y - screenHeight / 2.0f) / ((float) screenHeight / 2.0f));
                                if (b < 0.25) nShade = '#';
                                else if (b < 0.5) nShade = 'X';
                                else if (b < 0.75) nShade = '.';
                                else if (b < 0.9) nShade = '-';
                                else nShade = ' ';

                                textGraphics.putString(x, y, Character.toString((char) nShade));

                            }

                        }

                    }


                    //Stats
                    textGraphics.putString(0, mapWidth, "X=" + playerX + ", Y=" + playerY + ", A=" + playerA + ", FPS=" + 1.0f / elapsedTime, SGR.BOLD);

                    // Display map
                    for (int nx = 0; nx < mapWidth; nx++) {
                        for (int ny = 0; ny < mapWidth; ny++) {
                            textGraphics.putString(nx, ny, Character.toString(map.charAt(ny * mapWidth + nx)), SGR.BOLD);

                        }
                    }
                    // Player curent position
                    textGraphics.putString((int) playerX + 1, (int) playerY, "P", SGR.BOLD);
                    textGraphics.putString(screenWidth - 1, screenHeight - 1, " ");


                }


                terminal.flush();
                keyStroke = terminal.readInput();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
