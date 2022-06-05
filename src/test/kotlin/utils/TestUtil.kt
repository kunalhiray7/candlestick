package utils

import org.mockito.ArgumentCaptor

class TestUtil {

    companion object {
        fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
    }
}
