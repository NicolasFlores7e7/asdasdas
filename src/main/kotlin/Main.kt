package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import java.io.*


@Serializable
data class Address(
    val building: String,
    val coord: List<Double>,
    val street: String,
    val zipcode: String
) : java.io.Serializable

@Serializable
data class Grade(
    val date: String,
    val mark: String,
    val score: Int
) : java.io.Serializable

@Serializable
data class Restaurant(
    val address: Address,
    val borough: String,
    val cuisine: String,
    val grades: List<Grade>,
    val name: String,
    val restaurant_id: String
) : java.io.Serializable

fun main() {
    val restaurantsFile = File("src/main/kotlin/json/restaurants.json")
    val restaurantsFileBinary = File("src/main/kotlin/dat/restaurants.dat")
    val restaurantFileXML = File("src/main/kotlin/xml/restaurants.xml")

    ex1(restaurantsFile)
    ex2(restaurantsFile)
    ex3(restaurantsFileBinary)
    ex4(restaurantFileXML)
    ex5(restaurantFileXML)
    ex6(restaurantFileXML)
    ex7(restaurantFileXML)
}


fun ex1(restaurantsFile: File) {
// Recibimos el archivo JSon para leer y DESERIALIZAR

    //Creamos una lista mutable para guardar los valores de JSon
    val restaurants: MutableList<Restaurant> = mutableListOf()

    //Añadimos los productos del Json a nuestra lista e imrpimimos en consola la lista.
    println("Lista inicial de restaurantes: ")
    restaurantsFile.forEachLine { line ->
        // Decodificamos cada línea como un objeto Restaurant y lo agregamos a la lista
        val restaurant = Json.decodeFromString<Restaurant>(line)
        restaurants.add(restaurant)
        println(restaurant)
    }
}

fun ex2(restaurantsFile: File) {
    // Inicializa una lista mutable para almacenar los restaurantes
    val restaurants = mutableListOf<Restaurant>()

    // Itera sobre cada línea del archivo de restaurantes JSON
    restaurantsFile.forEachLine { line ->
        // Decodifica la línea JSON a un objeto Restaurant y lo agrega a la lista de restaurantes
        val restaurant = Json.decodeFromString<Restaurant>(line)
        restaurants.add(restaurant)
    }

    // Define la ruta del archivo de salida en formato binario
    val outputFile = File("src/main/kotlin/dat/restaurants.dat")

    // Abre un ObjectOutputStream para escribir los datos de los restaurantes en el archivo binario
    ObjectOutputStream(FileOutputStream(outputFile)).use { it.writeObject(restaurants) }

    // Imprime un mensaje indicando que los restaurantes se han guardado en el archivo binario
    println("Restaurantes guardados en ${outputFile.name}")
}


fun ex3(restaurantsBinaryFile: File) {
    // Inicializa un objeto InputStream para leer el archivo binario de restaurantes
    val inputStream = ObjectInputStream(FileInputStream(restaurantsBinaryFile))
    try {
        // Lee el objeto del archivo binario y lo convierte en una lista mutable de Restaurantes
        val restaurants = inputStream.readObject() as? MutableList<Restaurant> ?: mutableListOf()
        // Define la ruta del archivo XML de destino
        val xmlFilePath = File("src/main/kotlin/xml/restaurants.xml")
        // Inicializa un StringBuilder para construir la cadena XML
        val xmlString = StringBuilder()
        // Itera sobre cada restaurante y lo codifica a XML, agregándolo a xmlString
        restaurants.forEach { restaurant ->
            xmlString.append(XML.encodeToString(restaurant)).append("\n")
        }
        // Escribe la cadena XML en el archivo XML de destino
        xmlFilePath.writeText(xmlString.toString())
        // Imprime un mensaje indicando que los restaurantes se han guardado en el archivo XML
        println("Restaurantes guardados en ${xmlFilePath.name}")
    } catch (ex: EOFException) {
        // Captura una excepción si se alcanza el final del archivo binario
        println("Se alcanzó el final del archivo.")
    } catch (ex: IOException) {
        // Captura una excepción de entrada/salida y muestra el mensaje de error
        println("Error de entrada/salida: ${ex.message}")
    } finally {
        // Cierra el InputStream al finalizar
        inputStream.close()
    }
}

fun ex4(restaurantFileXML: File) {
    // Itera sobre cada línea del archivo XML
    restaurantFileXML.forEachLine { line ->
        // Decodifica la línea del XML a un objeto Restaurant y luego imprime su nombre
        val restaurant = XML.decodeFromString<Restaurant>(line)
        println(restaurant.name)
    }
}


fun ex5(restaurantFileXML: File) {
    // Crear tres nuevos restaurantes con sus valoraciones
    val newRestaurants = listOf(
        Restaurant(
            Address("6597", listOf(-1.14450, -452.10), "Av Vallcarca", "06597"),
            "Gracia",
            "Greek",
            listOf(Grade("2024-01-07", "S", 9)),
            "PapadoPoulo",
            "6569787"
        ),
        Restaurant(
            Address("127", listOf(3457.4545, -9834.7), "Paral·lel", "05564"),
            "Casc Antic",
            "Spanish Tapas",
            listOf(Grade("2022-03-10", "A", 7)),
            "TapaMala",
            "487993"
        ),
        Restaurant(
            Address("963321", listOf(-5545.332, 4688.457), "Av Vall D'Hebron", "06455"),
            "Horta-Guinardo",
            "French",
            listOf(Grade("2021-12-12", "F", 0)),
            "Gav A Cho",
            "86442"
        )
    )
    // Leer el contenido actual del archivo XML
    val currentContent = restaurantFileXML.readText()
    // Agregar los nuevos restaurantes al contenido actual
    val updatedContent = StringBuilder(currentContent)
    newRestaurants.forEach { restaurant ->
        updatedContent.append(XML.encodeToString(restaurant)).append("\n")
    }
    // Sobrescribir el archivo XML con el contenido actualizado
    restaurantFileXML.writeText(updatedContent.toString())

    println("Los restaurantes deseados han sido agregados al archivo ${restaurantFileXML.name}")
}

fun ex6(restaurantFileXML: File) {
    // Inicializa una cadena vacía para almacenar el XML modificado
    var xmlString = ""

    // Itera sobre cada línea del archivo XML
    restaurantFileXML.forEachLine { line ->
        // Decodifica la línea actual del XML a un objeto Restaurant
        val restaurant = XML.decodeFromString<Restaurant>(line)

        // Verifica si el ID del restaurante coincide con el ID específico (30075445)
        if (restaurant.restaurant_id == "30075445") {
            // Modifica la dirección del restaurante cambiando el código postal
            val modifiedAddress = restaurant.address.copy(zipcode = "10470")
            // Crea un nuevo objeto de restaurante con la dirección modificada
            val modifiedRestaurant = restaurant.copy(address = modifiedAddress)
            // Codifica el restaurante modificado a XML y lo agrega a la cadena xmlString
            xmlString += XML.encodeToString(modifiedRestaurant) + "\n"
        } else {
            // Si el ID no coincide, agrega la línea original sin cambios a xmlString
            xmlString += line + "\n"
        }
    }

    // Escribe la cadena xmlString en el archivo XML original
    restaurantFileXML.writeText(xmlString)

    // Imprime un mensaje indicando que se ha modificado y guardado el archivo
    println("Código postal modificado y guardado en el archivo ${restaurantFileXML.name}")
}
fun ex7(restaurantFileXML: File) {
    // Inicializa una cadena vacía para almacenar el XML modificado
    var xmlString = ""

    // Itera sobre cada línea del archivo XML
    restaurantFileXML.forEachLine { line ->
        // Decodifica la línea actual del XML a un objeto Restaurant
        val restaurant = XML.decodeFromString<Restaurant>(line)

        // Verifica si el ID del restaurante coincide con el ID específico (30191841)
        if (restaurant.restaurant_id == "30191841" && restaurant.grades.any { it.date == "1988-05-01" }) {
            // Encuentra el índice de la calificación con la fecha específica
            val index = restaurant.grades.indexOfFirst { it.date == "1988-05-01" }
            // Modifica el "mark" de la calificación
            val modifiedGrades = restaurant.grades.toMutableList()
            modifiedGrades[index] = modifiedGrades[index].copy(mark = "C")
            // Crea un nuevo objeto de restaurante con las calificaciones modificadas
            val modifiedRestaurant = restaurant.copy(grades = modifiedGrades)
            // Codifica el restaurante modificado a XML y lo agrega a la cadena xmlString
            xmlString += XML.encodeToString(modifiedRestaurant) + "\n"
        } else {
            // Si el ID no coincide o la fecha no está presente, agrega la línea original sin cambios a xmlString
            xmlString += line + "\n"
        }
    }

    // Escribe la cadena xmlString en el nuevo archivo XML "restaurantsMod.xml"
    val outputFile = File("src/main/kotlin/xml/restaurantsMod.xml")
    outputFile.writeText(xmlString)

    // Imprime un mensaje indicando que se ha modificado y guardado el nuevo archivo XML
    println("Datos modificados guardados en el archivo ${outputFile.name}")
}




