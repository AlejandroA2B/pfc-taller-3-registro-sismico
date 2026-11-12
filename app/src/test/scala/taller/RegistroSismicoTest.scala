package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import Datos.registro

@RunWith(classOf[JUnitRunner])
class RegistroSismicoTest extends AnyFunSuite {

  val s = new RegistroSismico()
  import s._

  val vacio: List[Sismo] = List()

  // Punto 1 -------------------------------------------------------------------

  test("fuertes: los que superan 5.0, en orden") {
    assert(fuertes(registro, 5.0) == List("Santander", "Choco", "Valle"))
  }

  test("fuertes: con un umbral alto no queda ninguno") {
    assert(fuertes(registro, 9.0) == List())
  }

  test("fuertes: conserva los repetidos") {
    assert(fuertes(registro, 3.0).length == 7)
  }

  test("fuertes: el umbral es estricto") {
    // Hay un sismo de magnitud 6.0 exacta; con umbral 6.0 no debe salir.
    assert(!fuertes(registro, 6.0).contains("Choco"))
  }

  test("enRegion: las magnitudes del Valle") {
    assert(enRegion(registro, "Valle") == List(4.2, 3.8, 5.3))
  }

  test("enRegion: una región que no está da la lista vacía") {
    assert(enRegion(registro, "Amazonas") == List())
  }

  test("enRegion: sobre el registro vacío no hay magnitudes") {
    assert(enRegion(vacio, "Valle") == List())
  }

  test("paresCercanos: con 0.6 solo Valle y Santander") {
    assert(paresCercanos(registro, 0.6) == List(("Valle", "Santander")))
  }

  test("paresCercanos: con 1.0 salen las tres parejas, en orden de aparición") {
    assert(paresCercanos(registro, 1.0) ==
      List(("Valle", "Santander"), ("Valle", "Choco"), ("Santander", "Choco")))
  }

  test("paresCercanos: con 0.1 no hay ninguna") {
    assert(paresCercanos(registro, 0.1) == List())
  }

  test("paresCercanos: cada pareja sale una sola vez") {
    val ps = paresCercanos(registro, 1.0)
    assert(ps.map { case (a, b) => Set(a, b) }.distinct.length == ps.length)
  }

  test("paresCercanos: nunca empareja una región consigo misma") {
    assert(paresCercanos(registro, 5.0).forall { case (a, b) => a != b })
  }

  //Mis test punto 1
  //Fuertes
  test("fuertes: devuelve lista vacía si el umbral es mayor que todas las magnitudes") {
    assert(fuertes(registro, 10.0) == List())
  }
  test("fuertes: umbral estricto, si la magnitud es exactamente igual al umbral no se incluye") {
    // Choco tiene un sismo de 6.0. Con umbral 6.0 NO debe salir.
    assert(fuertes(registro, 6.0) == List())
  }
  test("fuertes: mantiene los duplicados de región si hay varios sismos fuertes") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 5.0, 10),
      Sismo(Fecha(2025,1), "A", 5.5, 10)
    )
    assert(fuertes(r, 4.0) == List("A", "A"))
  }
  test("fuertes: respeta el orden de aparición original") {
    val r = List(
      Sismo(Fecha(2025,1), "Z", 6.0, 10),
      Sismo(Fecha(2025,1), "A", 7.0, 10)
    )
    assert(fuertes(r, 5.0) == List("Z", "A"))
  }
  test("fuertes: registro vacío") {
    assert(fuertes(vacio, 5.0) == List())
  }
  test("fuertes: umbral negativo incluye todos los sismos") {
    assert(fuertes(registro, -1.0).length == 7)
  }
  //enRegion
  test("enRegion: región inexistente devuelve lista vacía") {
    assert(enRegion(registro, "Amazonas") == List())
  }
  test("enRegion: conserva el orden de las magnitudes de los sismos") {
    assert(enRegion(registro, "Valle") == List(4.2, 3.8, 5.3))
  }
  test("enRegion: devuelve todas las magnitudes sin importar si están repetidas") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 4.0, 20)
    )
    assert(enRegion(r, "A") == List(4.0, 4.0))
  }
  test("enRegion: registro vacío devuelve lista vacía") {
    assert(enRegion(vacio, "Valle") == List())
  }
  test("enRegion: funciona con regiones con espacios o minúsculas exactas") {
    assert(enRegion(registro, "valle") == List()) // Case sensitive
  }
  test("enRegion: extrae correctamente magnitudes con decimales largos") {
    val r = List(Sismo(Fecha(2025,1), "A", 4.123456, 10))
    assert(enRegion(r, "A") == List(4.123456))
  }

  //paresCercanos
  test("paresCercanos: no repite parejas en orden inverso (A,B) vs (B,A)") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "B", 4.05, 10)
    )
    // A aparece antes que B, solo debe salir (A, B)
    assert(paresCercanos(r, 0.1) == List(("A", "B")))
  }
  test("paresCercanos: incluye el límite exacto de diferencia (<= dif)") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "B", 5.0, 10)
    )
    assert(paresCercanos(r, 1.0) == List(("A", "B")))
  }
  test("paresCercanos: no empareja una región consigo misma") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 4.05, 10)
    )
    assert(paresCercanos(r, 0.1) == List())
  }
  test("paresCercanos: dif negativa no genera ninguna pareja") {
    assert(paresCercanos(registro, -1.0) == List())
  }
  test("paresCercanos: registro vacío") {
    assert(paresCercanos(vacio, 1.0) == List())
  }
  test("paresCercanos: con una sola región no hay parejas posibles") {
    val r = List(Sismo(Fecha(2025,1), "A", 4.0, 10))
    assert(paresCercanos(r, 1.0) == List())
  }



  // Punto 2 -------------------------------------------------------------------

  test("porRegion: agrupa los siete sismos en tres regiones") {
    assert(porRegion(registro).keySet == Set("Valle", "Santander", "Choco"))
    assert(porRegion(registro).values.map(_.length).sum == 7)
  }

  test("conteoPorRegion: tres, dos y dos") {
    assert(conteoPorRegion(registro) ==
      Map("Valle" -> 3, "Santander" -> 2, "Choco" -> 2))
  }

  test("magnitudMaxima: la mayor de cada región") {
    assert(magnitudMaxima(registro) ==
      Map("Valle" -> 5.3, "Santander" -> 5.1, "Choco" -> 6.0))
  }

  test("conteoPorRegion: el registro vacío da el mapa vacío") {
    assert(conteoPorRegion(vacio) == Map())
  }

  test("magnitudMaxima: las regiones ausentes no aparecen") {
    assert(!magnitudMaxima(registro).contains("Amazonas"))
  }

  //Mis tests punto 2
  //porRegion
  test("porRegion: conserva el orden original de los sismos dentro de cada región") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 3.0, 10),
      Sismo(Fecha(2025,1), "B", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 5.0, 10)
    )
    // La lista de "A" debe tener primero el 3.0 y luego el 5.0, tal como entraron
    assert(porRegion(r)("A").map(_.magnitud) == List(3.0, 5.0))
  }

  test("porRegion: agrupa correctamente sismos con la misma fecha pero distinta región") {
    val r = List(
      Sismo(Fecha(2025,5), "Norte", 4.0, 10),
      Sismo(Fecha(2025,5), "Sur", 4.5, 20)
    )
    assert(porRegion(r).keySet == Set("Norte", "Sur"))
    assert(porRegion(r)("Norte").length == 1)
  }

  test("porRegion: un registro vacío debe devolver un mapa vacío, no un error") {
    assert(porRegion(vacio).isEmpty)
  }

  test("porRegion: funciona con una sola región y múltiples sismos") {
    val r = List(
      Sismo(Fecha(2025,1), "Unica", 2.0, 10),
      Sismo(Fecha(2025,1), "Unica", 3.0, 20),
      Sismo(Fecha(2025,1), "Unica", 4.0, 30)
    )
    assert(porRegion(r).size == 1)
    assert(porRegion(r)("Unica").length == 3)
  }

  test("porRegion: distingue entre mayúsculas y minúsculas en los nombres de región") {
    val r = List(
      Sismo(Fecha(2025,1), "Valle", 3.0, 10),
      Sismo(Fecha(2025,1), "valle", 4.0, 10)
    )
    assert(porRegion(r).size == 2) // Deben ser dos llaves distintas
  }

  test("porRegion: no altera los objetos Sismo originales (inmutabilidad)") {
    val s1 = Sismo(Fecha(2025,1), "A", 4.0, 10)
    val r = List(s1)
    val agrupado = porRegion(r)
    assert(agrupado("A").head eq s1) // Debe ser exactamente la misma referencia
  }

  //conteoPorRegion
  test("conteoPorRegion: devuelve ceros implícitos no, solo incluye las regiones existentes") {
    val r = List(Sismo(Fecha(2025,1), "A", 4.0, 10))
    val conteo = conteoPorRegion(r)
    assert(!conteo.contains("B")) // "B" no debe existir, ni siquiera con valor 0
  }

  test("conteoPorRegion: cuenta correctamente un sismo por cada región") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "B", 4.0, 10),
      Sismo(Fecha(2025,1), "C", 4.0, 10)
    )
    assert(conteoPorRegion(r).values.sum == 3) // 1 + 1 + 1
    assert(conteoPorRegion(r)("B") == 1)
  }

  test("conteoPorRegion: registro vacío devuelve Map()") {
    assert(conteoPorRegion(vacio).isEmpty)
  }

  test("conteoPorRegion: cuenta correctamente cuando todos los sismos son de la misma región") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 5.0, 10),
      Sismo(Fecha(2025,1), "A", 6.0, 10)
    )
    assert(conteoPorRegion(r).size == 1)
    assert(conteoPorRegion(r)("A") == 3)
  }

  test("conteoPorRegion: la suma de los valores debe ser igual al tamaño del registro") {
    assert(conteoPorRegion(registro).values.sum == registro.length)
  }

  test("conteoPorRegion: maneja nombres de región con espacios o caracteres especiales") {
    val r = List(
      Sismo(Fecha(2025,1), "San Andrés", 4.0, 10),
      Sismo(Fecha(2025,1), "San Andrés", 4.0, 10)
    )
    assert(conteoPorRegion(r)("San Andrés") == 2)
  }

  //magnitudMaxima
  test("magnitudMaxima: el valor máximo es correcto sin importar el orden") {
    val r1 = List(
      Sismo(Fecha(2025,1), "A", 3.0, 10),
      Sismo(Fecha(2025,1), "A", 6.5, 10)
    )
    val r2 = List(
      Sismo(Fecha(2025,1), "A", 6.5, 10),
      Sismo(Fecha(2025,1), "A", 3.0, 10)
    )
    assert(magnitudMaxima(r1)("A") == 6.5)
    assert(magnitudMaxima(r2)("A") == 6.5)
  }

  test("magnitudMaxima: no incluye regiones ausentes (no hay valores por defecto)") {
    val maxMap = magnitudMaxima(registro)
    assert(!maxMap.contains("Amazonas"))
  }

  test("magnitudMaxima: un registro vacío devuelve un mapa vacío") {
    assert(magnitudMaxima(vacio).isEmpty)
  }

  test("magnitudMaxima: maneja correctamente sismos con magnitudes idénticas") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.5, 10),
      Sismo(Fecha(2025,1), "A", 4.5, 20)
    )
    assert(magnitudMaxima(r)("A") == 4.5)
  }

  test("magnitudMaxima: conserva la precisión de los decimales largos") {
    val r = List(Sismo(Fecha(2025,1), "A", 6.78912, 10))
    assert(magnitudMaxima(r)("A") == 6.78912)
  }

  test("magnitudMaxima: calcula bien si hay varias regiones con máximos distintos") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 2.0, 10),
      Sismo(Fecha(2025,1), "A", 3.5, 10),
      Sismo(Fecha(2025,1), "B", 7.1, 10),
      Sismo(Fecha(2025,1), "B", 6.0, 10)
    )
    val res = magnitudMaxima(r)
    assert(res("A") == 3.5)
    assert(res("B") == 7.1)
  }

  // Punto 3 -------------------------------------------------------------------

  test("conteoSeguro: una región desconocida responde cero") {
    assert(conteoSeguro(registro)("Amazonas") == 0)
  }

  test("conteoSeguro: una región conocida responde su conteo") {
    assert(conteoSeguro(registro)("Valle") == 3)
  }

  test("conteoSeguro: sobre el registro vacío todo responde cero") {
    assert(conteoSeguro(vacio)("Valle") == 0)
  }

  test("maximaDe: el Chocó llega a 6.0") {
    assert(maximaDe(registro, "Choco") == Some(6.0))
  }

  test("maximaDe: una región sin registros da vacío") {
    assert(maximaDe(registro, "Amazonas") == None)
  }

  test("regionMasActiva: el Valle, con tres sismos") {
    assert(regionMasActiva(registro) == Some("Valle"))
  }

  test("regionMasActiva: sobre el registro vacío da vacío") {
    assert(regionMasActiva(vacio) == None)
  }

  test("regionMasActiva: en empate gana la primera alfabéticamente") {
    val empate = List(
      Sismo(Fecha(2025, 1), "Zulia", 4.0, 10),
      Sismo(Fecha(2025, 1), "Arauca", 4.0, 10))
    assert(regionMasActiva(empate) == Some("Arauca"))
  }

  //Mis tests punto 3
  //conteoSeguro
  test("conteoSeguro: devuelve 0 para una región inexistente sin lanzar excepción") {
    // Valida que el mapa con valor por defecto funciona
    assert(conteoSeguro(registro)("Guajira") == 0)
  }

  test("conteoSeguro: no altera el mapa original agregando la llave consultada") {
    val mapaSeguro = conteoSeguro(registro)
    mapaSeguro("Amazonas") // Hacemos la consulta fantasma
    // El mapa no debe haber mutado ni agregado la llave
    assert(!mapaSeguro.contains("Amazonas"))
  }

  test("conteoSeguro: funciona correctamente si hay una región con un solo sismo") {
    val r = List(Sismo(Fecha(2025,1), "Unica", 4.0, 10))
    assert(conteoSeguro(r)("Unica") == 1)
  }

  test("conteoSeguro: un registro vacío devuelve 0 para cualquier consulta") {
    assert(conteoSeguro(vacio)("CualquierRegion") == 0)
  }

  test("conteoSeguro: consultar una región que sí existe devuelve su conteo real, no 0") {
    assert(conteoSeguro(registro)("Santander") == 2)
  }

  test("conteoSeguro: es case-sensitive, una mayúscula diferente devuelve 0") {
    // "valle" no es igual a "Valle"
    assert(conteoSeguro(registro)("valle") == 0)
  }

 //maximaDe
  test("maximaDe: devuelve None para una región que no está en el registro") {
    assert(maximaDe(registro, "Atlantico") == None)
  }

  test("maximaDe: devuelve Some con el valor exacto para una región conocida") {
    assert(maximaDe(registro, "Valle") == Some(5.3))
  }

  test("maximaDe: el máximo no depende del orden de inserción de los sismos") {
    val r1 = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 7.5, 10)
    )
    val r2 = List(
      Sismo(Fecha(2025,1), "A", 7.5, 10),
      Sismo(Fecha(2025,1), "A", 4.0, 10)
    )
    assert(maximaDe(r1, "A") == Some(7.5))
    assert(maximaDe(r2, "A") == Some(7.5))
  }

  test("maximaDe: funciona correctamente si la región solo tiene un sismo") {
    val r = List(Sismo(Fecha(2025,1), "B", 3.14, 10))
    assert(maximaDe(r, "B") == Some(3.14))
  }

  test("maximaDe: un registro vacío devuelve None para cualquier región") {
    assert(maximaDe(vacio, "Valle") == None)
  }

  test("maximaDe: tolera decimales largos sin perder precisión en el Some") {
    val r = List(Sismo(Fecha(2025,1), "C", 5.987654, 10))
    assert(maximaDe(r, "C") == Some(5.987654))
  }

  //region mas activa
  test("regionMasActiva: caso base con un claro ganador") {
    assert(regionMasActiva(registro) == Some("Valle"))
  }
  test("regionMasActiva: registro vacío devuelve None") {
    assert(regionMasActiva(vacio) == None)
  }
  test("regionMasActiva: empate de 2 regiones, gana la primera alfabéticamente") {
    val r = List(
      Sismo(Fecha(2025,1), "Zeta", 4.0, 10),
      Sismo(Fecha(2025,1), "Alfa", 4.0, 10)
    )
    assert(regionMasActiva(r) == Some("Alfa"))
  }
  test("regionMasActiva: empate de 3 regiones, gana la primera alfabéticamente") {
    val r = List(
      Sismo(Fecha(2025,1), "C", 4.0, 10),
      Sismo(Fecha(2025,1), "B", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 4.0, 10)
    )
    assert(regionMasActiva(r) == Some("A"))
  }
  test("regionMasActiva: gana por cantidad aunque su nombre sea mayor alfabéticamente") {
    val r = List(
      Sismo(Fecha(2025,1), "Z", 4.0, 10),
      Sismo(Fecha(2025,1), "Z", 4.0, 10),
      Sismo(Fecha(2025,1), "A", 4.0, 10)
    )
    assert(regionMasActiva(r) == Some("Z"))
  }
  test("regionMasActiva: una sola región es la más activa por defecto") {
    val r = List(Sismo(Fecha(2025,1), "Unica", 4.0, 10))
    assert(regionMasActiva(r) == Some("Unica"))
  }


  // Punto 4 -------------------------------------------------------------------

  test("replicas: las primeras cuatro de un sismo de 5.0") {
    assert(replicas(5.0).take(4).toList == List(5.0, 4.0, 3.2, 2.56))
  }

  test("replicas: la secuencia es infinita y no se cuelga") {
    assert(replicas(5.0).take(200).toList.length == 200)
  }

  test("replicas: empieza en la magnitud dada") {
    assert(replicas(7.3).head == 7.3)
  }

  test("replicas: los valores van redondeados a dos decimales") {
    assert(replicas(5.0).take(6).toList.forall(x => x == Math.round(x * 100.0) / 100.0))
  }

  test("replicasSensibles: las que superan 3.0 partiendo de 5.0") {
    assert(replicasSensibles(5.0, 3.0) == List(5.0, 4.0, 3.2))
  }

  test("replicasSensibles: si la primera no supera el mínimo, no hay ninguna") {
    assert(replicasSensibles(2.0, 3.0) == List())
  }

  test("replicasSensibles: el mínimo es estricto") {
    assert(replicasSensibles(3.0, 3.0) == List())
  }

  //Mis tests punto 4
  //replicas
  test("replicas: multiplica por 0.8 y redondea en cada paso") {
    assert(replicas(5.0).take(5).toList == List(5.0, 4.0, 3.2, 2.56, 2.05))
  }
  test("replicas: secuencia infinita no cuelga y se puede pedir un pedazo grande") {
    assert(replicas(10.0).take(100).length == 100)
  }
  test("replicas: el redondeo no acumula error flotante") {
    // Si no se redondeara, el valor podría ser 2.5600000000000005
    assert(replicas(5.0).take(4).toList.last == 2.56)
  }
  test("replicas: con magnitud 0, se queda en 0") {
    assert(replicas(0.0).take(3).toList == List(0.0, 0.0, 0.0))
  }
  test("replicas: empieza exactamente con la magnitud dada") {
    assert(replicas(7.3).head == 7.3)
  }
  test("replicas: funciona con magnitudes decimales") {
    assert(replicas(2.5).take(2).toList == List(2.5, 2.0))
  }

  //Replicas sensibles
  test("replicasSensibles: devuelve vacío si la primera no supera el mínimo") {
    assert(replicasSensibles(2.0, 3.0) == List())
  }
  test("replicasSensibles: el mínimo es estricto (>) y no incluye el igual") {
    //Aqui en esta parte 3.2 no supera a 3.2, por lo que se detiene antes
    assert(replicasSensibles(4.0, 3.2) == List(4.0))
  }

  test("replicasSensibles: termina correctamente en el borde exacto") {
    assert(replicasSensibles(5.0, 2.56) == List(5.0, 4.0, 3.2))
  }

  test("replicasSensibles: caso de un solo elemento porque el mínimo es muy alto") {
    assert(replicasSensibles(5.0, 4.5) == List(5.0))
  }

  test("replicasSensibles: respeta el redondeo a dos decimales en la condición del mínimo") {
    // Las réplicas que hay de 5.0 son: 5.0, 4.0, 3.2, 2.56, 2.05... hasta el infinito
    // Pero si el mínimo es 2.56, la condición es estricta (>),Por lo tanto 2.56 no entra.
    // Pero el anterior a ese en este caso  es 3.2, por lo tanto sí entra.
    assert(replicasSensibles(5.0, 2.56) == List(5.0, 4.0, 3.2))
    // Si el mínimo en este caso es 2.55, entonces 2.56 en este caso si superaria el mínimo por lo tanto debe entrar.
    assert(replicasSensibles(5.0, 2.55) == List(5.0, 4.0, 3.2, 2.56))
  }

  // Punto 5 -------------------------------------------------------------------

  test("informe: el resumen de las tres regiones") {
    assert(informe(registro, 3.0) ==
      Map("Valle" -> Resumen(3, 5.3, 3),
          "Santander" -> Resumen(2, 5.1, 3),
          "Choco" -> Resumen(2, 6.0, 4)))
  }

  test("informe: el registro vacío da el mapa vacío") {
    assert(informe(vacio, 3.0) == Map())
  }

  test("informe: el conteo coincide con conteoPorRegion") {
    assert(informe(registro, 3.0).map { case (r, res) => (r, res.sismos) } ==
      conteoPorRegion(registro))
  }

  test("informe: la máxima coincide con magnitudMaxima") {
    assert(informe(registro, 3.0).map { case (r, res) => (r, res.maxima) } ==
      magnitudMaxima(registro))
  }

  test("informe: las réplicas coinciden con replicasSensibles del más fuerte") {
    assert(informe(registro, 3.0)("Choco").replicas ==
      replicasSensibles(6.0, 3.0).length)
  }

  //Mis tests punto 5
  //informe
  test("informe: cálculo correcto para un registro pequeño") {
    val r = List(
      Sismo(Fecha(2025,1), "A", 4.0, 10),
      Sismo(Fecha(2025,1), "B", 5.0, 10),
      Sismo(Fecha(2025,1), "B", 6.0, 10)
    )
    val res = informe(r, 3.0)
    assert(res("A") == Resumen(1, 4.0, 2)) // 4.0, 3.2 (2 repl)
    assert(res("B") == Resumen(2, 6.0, 4)) // 6.0, 4.8, 3.84, 3.07 (4 repl)
  }
  test("informe: el conteo es 0 réplicas si el máximo no supera el mínimo") {
    val r = List(Sismo(Fecha(2025,1), "A", 2.0, 10))
    assert(informe(r, 3.0)("A").replicas == 0)
  }
  test("informe: registro vacío devuelve mapa vacío") {
    assert(informe(vacio, 3.0) == Map())
  }
  test("informe: la máxima reportada es realmente la máxima") {
    assert(informe(registro, 3.0)("Valle").maxima == 5.3)
  }
  test("informe: el conteo reportado es realmente el total de sismos") {
    assert(informe(registro, 3.0)("Valle").sismos == 3)
  }
  test("informe: las réplicas se calculan sobre la máxima, no sobre otra") {
    // Choco tiene máx 6.0. replicasSensibles(6.0, 3.0) = List(6.0, 4.8, 3.84, 3.07) -> 4
    assert(informe(registro, 3.0)("Choco").replicas == 4)
  }

}
