package com.moneyguardian;

import android.content.Context;
import android.os.Parcel;
import android.util.Pair;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.moneyguardian.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Usuario usuario;
    private final String testName = "Test";
    private final String testEmail = "test@test.com";

    @Before
    public void build() {
        usuario = new Usuario();
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.moneyguardian", appContext.getPackageName());
    }

    @Test
    public void usuario_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        usuario.setNombre(testName);
        usuario.setCorreo(testEmail);
        usuario.setAmigos(new ArrayList<>());
        usuario.setMisPagosConjuntos(new ArrayList<>());

        // Write the data.
        Parcel parcel = Parcel.obtain();
        usuario.writeToParcel(parcel, usuario.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        Usuario createdFromParcel = Usuario.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        assertTrue(createdFromParcel.getNombre().equals(testName));
        assertTrue(createdFromParcel.getCorreo().equals(testEmail));
        assertTrue(createdFromParcel.getAmigos().isEmpty());
        assertTrue(createdFromParcel.getMisPagosConjuntos().isEmpty());
    }

}