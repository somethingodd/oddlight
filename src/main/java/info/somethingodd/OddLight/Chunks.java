/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.somethingodd.OddLight;

import info.somethingodd.OddLight.configuration.Lights;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class Chunks {
    private static final Map<Chunk, Lights> chunks;

    static {
        chunks = new HashMap<Chunk, Lights>();
    }

    public static Lights getChunk(Chunk chunk) {
        return chunks.get(chunk);
    }

    public static void putChunk(ChunkSnapshot chunkSnapshot) {
        chunks.put(Bukkit.getWorld(chunkSnapshot.getWorldName()).getChunkAt(chunkSnapshot.getX(), chunkSnapshot.getZ()), new Lights(chunkSnapshot));
    }

    public static void putChunk(Chunk chunk, Lights lights) {
        chunks.put(chunk, lights);
    }

    public static void rainOn(Chunk chunk) {
        chunks.get(chunk).rainOn();
    }
}