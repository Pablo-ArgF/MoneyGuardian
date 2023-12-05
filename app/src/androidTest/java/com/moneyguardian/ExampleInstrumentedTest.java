package com.moneyguardian;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.moneyguardian.modelo.GrupoUsuarios;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    private PagoConjunto pagoConjunto;
    private final Date testDate = new Date();
    private final Uri imagen = Uri.parse("test");
    private final List<UsuarioParaParcelable> testParticipants = new ArrayList<>();
    private final List<ItemPagoConjunto> testItems = new ArrayList<>();
    private ItemPagoConjunto itemPagoConjunto;
    private final HashMap<UsuarioParaParcelable, Double> testPagos = new HashMap<>();

    private GrupoUsuarios grupoUsuarios;
    private final List<Usuario> testUsuarios = new ArrayList<>();


    @Before
    public void setUp() {
        usuario = new Usuario();
        pagoConjunto = new PagoConjunto("id", testName, testDate, testParticipants, imagen, testDate);
        // Initialize the ItemPagoConjunto object using the constructor
        itemPagoConjunto = new ItemPagoConjunto(UUID.randomUUID().toString(),testName, testPagos,null);
        // Initialize the GrupoUsuarios object using the constructor
        grupoUsuarios = new GrupoUsuarios(testName, testUsuarios);
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

    @Test
    public void pagoConjunto_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        pagoConjunto.writeToParcel(parcel, pagoConjunto.describeContents());

        // After writing, reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        PagoConjunto createdFromParcel = PagoConjunto.CREATOR.createFromParcel(parcel);

        // Verify the received data.
        assertEquals(testName, createdFromParcel.getNombre());
        assertEquals(testDate, createdFromParcel.getFechaPago());
        assertTrue(createdFromParcel.getParticipantes().isEmpty());
        assertTrue(createdFromParcel.getItems().isEmpty());
    }

    @Test
    public void pagoConjuntoNotEmpty_ParcelableWriteRead() {
        UsuarioParaParcelable usuario = new UsuarioParaParcelable(testName, testEmail);
        testParticipants.add(usuario);
        // Write the data.
        Parcel parcel = Parcel.obtain();
        pagoConjunto.writeToParcel(parcel, pagoConjunto.describeContents());

        // After writing, reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        PagoConjunto createdFromParcel = PagoConjunto.CREATOR.createFromParcel(parcel);

        // Verify the received data.
        assertTrue(!createdFromParcel.getParticipantes().isEmpty());
        assertEquals(createdFromParcel.getParticipantes().get(0), usuario);
    }


    @Test
    public void itemPagoConjunto_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        itemPagoConjunto.writeToParcel(parcel, itemPagoConjunto.describeContents());

        // After writing, reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ItemPagoConjunto createdFromParcel = ItemPagoConjunto.CREATOR.createFromParcel(parcel);

        // Verify the received data.
        assertEquals(testName, createdFromParcel.getNombre());
        assertTrue(createdFromParcel.getPagos().isEmpty());
    }

    @Test
    public void grupoUsuarios_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        grupoUsuarios.writeToParcel(parcel, grupoUsuarios.describeContents());

        // After writing, reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        GrupoUsuarios createdFromParcel = GrupoUsuarios.CREATOR.createFromParcel(parcel);

        // Verify the received data.
        assertEquals(testName, createdFromParcel.getNombre());
        assertTrue(createdFromParcel.getUsuarios().isEmpty());
    }

    @Test
    public void grupoUsuariosNotEmpty_ParcelableWriteRead() {
        Usuario usuario = new Usuario("id", testName, testEmail, null, new ArrayList<>(), new ArrayList<>());
        testUsuarios.add(usuario);

        // Write the data.
        Parcel parcel = Parcel.obtain();
        grupoUsuarios.writeToParcel(parcel, grupoUsuarios.describeContents());

        // After writing, reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        GrupoUsuarios createdFromParcel = GrupoUsuarios.CREATOR.createFromParcel(parcel);

        // Verify the received data.
        assertTrue(!createdFromParcel.getUsuarios().isEmpty());
        assertEquals(createdFromParcel.getUsuarios().get(0), usuario);
    }

}