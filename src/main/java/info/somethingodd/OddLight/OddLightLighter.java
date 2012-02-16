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

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightLighter {
    private boolean _consumed;
    private int _duration;

    public OddLightLighter(boolean consumed, int duration) {
        _consumed = consumed;
        _duration = duration;
    }

    public boolean consumed() {
        return _consumed;
    }

    public int duration() {
        return _duration;
    }

    public int hashCode() {
        int hash = 37;
        if (consumed()) hash++;
        hash += duration();
        return hash;
    }

    public boolean equals(OddLightLighter other) {
        if (other.consumed() != consumed()) return false;
        return (other.duration() != duration());
    }

}
