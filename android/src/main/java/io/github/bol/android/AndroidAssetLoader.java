package io.github.bol.android;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

import android.content.Context;

import io.github.bol.AssetLoader;

public class AndroidAssetLoader implements AssetLoader {

    private Context context;

    public AndroidAssetLoader(Context context) {
        this.context = context;
    }

    public int[][][] loadWorldFromAssets(String filename) {

        try {
            InputStream inputStream = context.getAssets().open("levels/" + filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            Element sizeElement = (Element) doc.getElementsByTagName("Size").item(0);
            int width = Integer.parseInt(sizeElement.getAttribute("width"));
            int depth = Integer.parseInt(sizeElement.getAttribute("depth"));
            int height = Integer.parseInt(sizeElement.getAttribute("height"));

            // Ursprüngliche Welt (so wie im XML)
            int[][][] temp = new int[width][depth][height];

            NodeList layerList = doc.getElementsByTagName("Layer");
            for (int i = 0; i < layerList.getLength(); i++) {
                Element layerElement = (Element) layerList.item(i);
                int z = Integer.parseInt(layerElement.getAttribute("height"));

                NodeList rowList = layerElement.getElementsByTagName("Row");
                for (int j = 0; j < rowList.getLength(); j++) {
                    Element rowElement = (Element) rowList.item(j);
                    int y = Integer.parseInt(rowElement.getAttribute("y"));

                    String[] values = rowElement.getTextContent().trim().split(" ");
                    for (int x = 0; x < values.length; x++) {
                        temp[x][y][z] = Integer.parseInt(values[x]);
                    }
                }
            }

            // Jetzt flippen wir die Welt gleich hier (statt flipWorldData später)
            int[][][] flipped = new int[width][depth][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < depth; y++) {
                    for (int z = 0; z < height; z++) {
                        int flippedX = width - 1 - x;
                        int flippedY = depth - 1 - y;
                        int flippedZ = height - 1 - z;
                        flipped[flippedY][flippedX][flippedZ] = temp[x][y][z];
                    }
                }
            }

            System.out.println("Level geladen und geflippt: " + filename);
            return flipped;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }


}
