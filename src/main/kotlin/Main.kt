package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.*
import javax.xml.parsers.DocumentBuilderFactory

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
    val restaurants = mutableListOf<Restaurant>()
    restaurantsFile.forEachLine { line ->
        val restaurant = Json.decodeFromString<Restaurant>(line)
        restaurants.add(restaurant)
    }
    val outputFile = File("src/main/kotlin/dat/restaurants.dat")
    ObjectOutputStream(FileOutputStream(outputFile)).use { it.writeObject(restaurants) }
    println("Restaurantes guardados en ${outputFile.name}")
}


fun ex3(restaurantsBinaryFile: File) {
    var restaurants = mutableListOf<Restaurant>()
    // Abre un "flujo de inputs(InputStream)" desde el archivo proporcionado
    val inputStream = ObjectInputStream(FileInputStream(restaurantsBinaryFile))
    try {
        // Intenta leer la lista de restaurantes del archivo
        @Suppress("UNCHECKED_CAST")
        // Si no hay excepciones, lee la lista de restaurantes del archivo y la asigna a la variable `restaurants`
        restaurants = (inputStream.readObject() as? List<Restaurant>)?.toMutableList() ?: mutableListOf()
    } catch (ex: EOFException) {
        println("Se alcanzó el final del archivo.")
    } catch (ex: IOException) {
        println("Error de entrada/salida: ${ex.message}")
    } finally {
        // Cierra el inputStream
        inputStream.close()
    }
    // Escribe los restaurantes en un archivo XML
    val restaurantsFile = File("src/main/kotlin/xml/restaurants.xml")
    restaurantsFile.bufferedWriter().use { writer ->
        restaurants.forEach { restaurant ->
            with(writer) {
                append("<restaurant>")
                append("<name>${restaurant.name}</name>")
                append("<borough>${restaurant.borough}</borough>")
                append("<cuisine>${restaurant.cuisine}</cuisine>")
                append("<restaurant_id>${restaurant.restaurant_id}</restaurant_id>")
                append("<address>")
                append("<building>${restaurant.address.building}</building>")
                append("<coord>${restaurant.address.coord.joinToString()}</coord>")
                append("<street>${restaurant.address.street}</street>")
                append("<zipcode>${restaurant.address.zipcode}</zipcode>")
                append("</address>")
                append("<grades>")
                // Itera sobre cada calificación del restaurante para agregarla al XML
                restaurant.grades.forEach { grade ->
                    append("<grade>")
                    append("<date>${grade.date}</date>")
                    append("<mark>${grade.mark}</mark>")
                    append("<score>${grade.score}</score>")
                    append("</grade>")
                }
                append("</grades>")
                append("</restaurant>")
            }
            writer.newLine() // Nueva línea después de cada restaurante
        }
    }
    println("Restaurantes guardados en ${restaurantsFile.name}")
}


fun ex4(restaurantFileXML: File) {
    // Verificar si el archivo XML existe
    if (!restaurantFileXML.exists()) {
        println("El archivo ${restaurantFileXML.name} no existe.")
        return
    }

    try {
        // Crear un DocumentBuilder
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        // Leer el archivo XML línea por línea
        restaurantFileXML.forEachLine { line ->
            try {
                // Parsear la línea como un elemento XML
                val document = builder.parse(line.byteInputStream())

                // Obtener los nombres de los restaurantes de cada elemento <restaurant> y mostrarlos por pantalla
                val nodeList = document.getElementsByTagName("restaurant")
                for (i in 0 until nodeList.length) {
                    val node = nodeList.item(i)
                    if (node.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
                        val element = node as org.w3c.dom.Element
                        val name = element.getElementsByTagName("name").item(0).textContent
                        println(name)
                    }
                }
            } catch (e: Exception) {
                println("Error al procesar la línea del archivo XML: ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("Error al procesar el archivo XML: ${e.message}")
    }
}





