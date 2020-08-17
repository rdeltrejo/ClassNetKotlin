package br.classinformatica.vcandroid.classnetk

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.classinformatica.vcandroid.classnetk.entidades.Configuracoes
import br.classinformatica.vcandroid.classnetk.entidades.Servidores
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import java.lang.Exception

class Banco : SQLiteOpenHelper {

    companion object {
        var DATABASE_VERSION = 1
        var DATABASE_NAME = "vclass.db"
    }

    constructor(context: Context?) : super(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    )

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(
                "CREATE TABLE Configuracoes (" +
                        "con_proxy_link TEXT DEFAULT \"\", " +
                        "con_proxy_porta INTEGER DEFAULT 0, " +
                        "con_proxy_usu TEXT DEFAULT \"\", " +
                        "con_proxy_pwd TEXT DEFAULT \"\", " +
                        "con_inicia_login INTEGER DEFAULT 0, " +
                        "con_id_instituicao NUMERIC DEFAULT 0, " +
                        "ser_id INTEGER DEFAULT 0);"
            )

            db.execSQL(
                "CREATE TABLE Servidores (" +
                        "ser_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ser_link TEXT DEFAULT \"\");"
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            var cursor: Cursor

            if (isTableExists(db, "Configuracoes")) {
                cursor = db.rawQuery("SELECT * FROM Configuracoes LIMIT 1", null)
                if (cursor.getColumnIndex("con_proxy_link") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_proxy_link TEXT DEFAULT \"\"")
                }
                if (cursor.getColumnIndex("con_proxy_porta") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_proxy_porta INTEGER DEFAULT 0")
                }
                if (cursor.getColumnIndex("con_proxy_usu") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_proxy_usu TEXT DEFAULT \"\"")
                }
                if (cursor.getColumnIndex("con_proxy_pwd") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_proxy_pwd TEXT DEFAULT \"\"")
                }
                if (cursor.getColumnIndex("con_inicia_login") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_inicia_login INTEGER DEFAULT 0")
                }
                if (cursor.getColumnIndex("con_id_instituicao") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN con_id_instituicao NUMERIC DEFAULT 0")
                }
                if (cursor.getColumnIndex("ser_id") == -1) {
                    db.execSQL("ALTER TABLE Configuracoes ADD COLUMN ser_id INTEGER DEFAULT 0")
                }
                cursor.close()
            } else {
                db.execSQL("DROP TABLE IF EXISTS Configuracoes;")
                db.execSQL(
                    "CREATE TABLE Configuracoes (" +
                            "con_proxy_link TEXT DEFAULT \"\", " +
                            "con_proxy_porta INTEGER DEFAULT 0, " +
                            "con_proxy_usu TEXT DEFAULT \"\", " +
                            "con_proxy_pwd TEXT DEFAULT \"\", " +
                            "con_inicia_login INTEGER DEFAULT 0, " +
                            "con_id_instituicao NUMERIC DEFAULT 0, " +
                            "ser_id INTEGER DEFAULT 0);"
                )
            }

            if (isTableExists(db, "Servidores")) {
                cursor = db.rawQuery("SELECT * FROM Servidores LIMIT 1", null)
                if (cursor.getColumnIndex("ser_id") == -1) {
                    db.execSQL("DROP TABLE IF EXISTS Servidores;")
                    db.execSQL(
                        "CREATE TABLE Servidores (" +
                                "ser_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "ser_link TEXT DEFAULT \"\");"
                    )
                }
                if (cursor.getColumnIndex("ser_link") == -1) {
                    db.execSQL("ALTER TABLE Servidores ADD COLUMN ser_link TEXT DEFAULT \"\"")
                }
                cursor.close()
            } else {
                db.execSQL("DROP TABLE IF EXISTS Servidores;")
                db.execSQL(
                    "CREATE TABLE Servidores (" +
                            "ser_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "ser_link TEXT DEFAULT \"\");"
                )
            }
        }
    }

    //minhas funções ---------------------------------------------------------------------------------

    fun apagarServidor(ser_id: Int) {
        val db = writableDatabase
        try {
            db.delete("Servidores", "ser_id = ?", Array(1) { ser_id.toString() })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }
        db.close()
    }

    fun atualizarInstituicaoConfiguracoes(con_id_instituicao: Long): Boolean {
        var ret = false
        val db = writableDatabase
        try {
            var values = ContentValues()
            values.put("con_id_instituicao", con_id_instituicao)
            db.update("Configuracoes", values, null, null)
            ret = true
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }
        db.close()
        return ret
    }

    fun getConfiguracoes(): Configuracoes {
        val configuracoes = Configuracoes()

        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT con_proxy_link, con_proxy_porta, con_proxy_usu, con_proxy_pwd, con_inicia_login, con_id_instituicao, ser_id FROM Configuracoes",
            null
        )
        if (cursor.moveToNext()) {
            configuracoes.con_proxy_link = cursor.getString(cursor.getColumnIndex("con_proxy_link"))
            configuracoes.con_proxy_porta = cursor.getInt(cursor.getColumnIndex("con_proxy_porta"))
            configuracoes.con_proxy_usu = cursor.getString(cursor.getColumnIndex("con_proxy_usu"))
            configuracoes.con_proxy_pwd = cursor.getString(cursor.getColumnIndex("con_proxy_pwd"))
            configuracoes.con_inicia_login = true
            if (cursor.getInt(cursor.getColumnIndex("con_inicia_login")) == 0) {
                configuracoes.con_inicia_login = false
            }
            configuracoes.con_id_instituicao = cursor.getLong(cursor.getColumnIndex("con_id_instituicao"))
            configuracoes.ser_id = cursor.getInt(cursor.getColumnIndex("ser_id"))
        }
        cursor.close()
        db.close()

        return configuracoes
    }

    fun getListaServidores(): List<Servidores> {
        val servidores = ArrayList<Servidores>()
        var servidor: Servidores

        val db = readableDatabase
        val cursor = db.rawQuery("SELECT ser_id, ser_link FROM Servidores", null)
        while (cursor.moveToNext()) {
            servidor = Servidores()
            servidor.ser_id = cursor.getInt(cursor.getColumnIndex("ser_id"))
            servidor.ser_link = cursor.getString(cursor.getColumnIndex("ser_link"))
            servidores.add(servidor)
        }
        cursor.close()
        db.close()

        return servidores
    }

    fun getServidores(ser_id: Int): Servidores {
        val servidor = Servidores()

        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT ser_id, ser_link FROM Servidores WHERE ser_id = ?", Array(1) { ser_id.toString() })
        if (cursor.moveToNext()) {
            servidor.ser_id = cursor.getInt(cursor.getColumnIndex("ser_id"))
            servidor.ser_link = cursor.getString(cursor.getColumnIndex("ser_link"))
        }
        cursor.close()
        db.close()

        return servidor
    }

    fun getServidores(ser_link: String): Servidores {
        val servidor = Servidores()

        val db = readableDatabase
        val cursor = db.rawQuery("SELECT ser_id, ser_link FROM Servidores WHERE ser_link = ?", Array(1) { ser_link })
        if (cursor.moveToNext()) {
            servidor.ser_id = cursor.getInt(cursor.getColumnIndex("ser_id"))
            servidor.ser_link = cursor.getString(cursor.getColumnIndex("ser_link"))
        }
        cursor.close()
        db.close()

        return servidor
    }

    fun gravarConfiguracoes(configuracoes: Configuracoes): Boolean {
        var ret = false
        val db = writableDatabase
        try {
            db.delete("Configuracoes", null, null)
            val values = ContentValues()
            values.put("con_proxy_link", configuracoes.con_proxy_link)
            values.put("con_proxy_porta", configuracoes.con_proxy_porta)
            values.put("con_proxy_usu", configuracoes.con_proxy_usu)
            values.put("con_proxy_pwd", configuracoes.con_proxy_pwd)
            if (configuracoes.con_inicia_login) {
                values.put("con_inicia_login", 1)
            } else {
                values.put("con_inicia_login", 0)
            }
            values.put("con_id_instituicao", configuracoes.con_id_instituicao)
            values.put("ser_id", configuracoes.ser_id)
            db.insert("Configuracoes", null, values)
            ret = true
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }
        db.close()
        return ret
    }

    fun gravarServidores(servidores: Servidores): Servidores {
        val db = writableDatabase
        try {
            val values = ContentValues()
            values.put("ser_link", servidores.ser_link)
            servidores.ser_id = db.insert("Servidores", null, values).toInt()
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }
        db.close()
        return servidores
    }

    fun iniciarServidores(): Servidores {
        var servidor = Servidores()
        servidor.ser_link = UtilClass.URL_AMAZON

        val db = writableDatabase
        try {
            db.delete("Servidores", null, null)

            var values = ContentValues()
            values.put("ser_link", servidor.ser_link)
            servidor.ser_id = db.insert("Servidores", null, values).toInt()

            values = ContentValues()
            values.put("ser_link", UtilClass.URL_GEO)
            db.insert("Servidores", null, values)
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }
        db.close()

        return servidor
    }

    private fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor =
            db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?", Array(1) { tableName })
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }
}