package pl.jakobczyk.gps.tracker.producer.util;

import org.junit.jupiter.api.Test;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;

import static org.assertj.core.api.Assertions.assertThat;

class GpsCoordUtilTest {


    @Test
    void simulateMovement_shouldChangeCoordinates() {
        // given
        GpsCoord initialCoord = new GpsCoord(50.0, 50.0);

        // when
        GpsCoord newCoord = GpsCoordUtil.simulateMovement(initialCoord);

        // then
        assertThat(initialCoord.latitude()).isNotEqualTo(newCoord.latitude());
        assertThat(initialCoord.longitude()).isNotEqualTo(newCoord.longitude());
    }


    @Test
    void generateRandomGpsCoord_shouldReturnCoordinatesInRange() {
        // given
        GpsCoord coord = GpsCoordUtil.generateRandomGpsCoord();

        // when then
        assertThat(coord.latitude()).isBetween(-70.0, 70.0);
        assertThat(coord.longitude()).isBetween(-140.0, 140.0);
    }
}
