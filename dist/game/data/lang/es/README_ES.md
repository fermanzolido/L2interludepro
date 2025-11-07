# Soporte Multiidioma - Español (ES)

## Descripción

Este directorio contiene todos los archivos de localización en español para el servidor L2J Mobius Interlude.

## Estructura de Archivos

### Archivos XML de Localización

1. **NpcNameLocalisation.xml**
   - Contiene las traducciones de nombres y títulos de NPCs
   - Sintaxis: `<localisation id="ID_NPC" name="Nombre" title="Título" />`
   - Ejemplo: `<localisation id="30080" name="Clarisa" title="Teletransportador" />`

2. **SystemMessageLocalisation.xml**
   - Contiene las traducciones de mensajes del sistema
   - Sintaxis: `<localisation id="ID_MENSAJE" translation="Texto traducido" />`
   - Ejemplo: `<localisation id="34" translation="Bienvenido al mundo de Lineage II." />`

3. **SendMessageLocalisation.xml**
   - Contiene traducciones de mensajes personalizados enviados al jugador
   - Sintaxis: `<localisation message="Mensaje original" translation="Traducción" />`
   - Ejemplo: `<localisation message="You have learned XXX new skills." translation="Has aprendido XXX nuevas habilidades." />`
   - Nota: "XXX" se usa como marcador de posición para valores dinámicos

### Archivos HTML de Interfaz

Ubicados en `data/html/mods/Lang/`:

- **LanguageSelect.htm**: Interfaz de selección de idioma
- **Ok.htm**: Mensaje de confirmación al cambiar idioma
- **Error.htm**: Mensaje de error si falla el cambio de idioma

## Cómo Usar

### Para Jugadores

Los jugadores pueden cambiar el idioma usando el comando:
```
.lang es
```

O sin parámetros para ver la lista de idiomas disponibles:
```
.lang
```

### Para Administradores

1. **Activar el soporte multiidioma**:
   - Editar `/dist/game/config/Custom/MultilingualSupport.ini`
   - Establecer `MultiLangEnable = True`
   - Asegurarse de que "es" esté en `MultiLangAllowed`

2. **Añadir más traducciones**:
   - Editar los archivos XML correspondientes
   - Seguir la sintaxis especificada en cada archivo
   - Reiniciar el servidor para aplicar cambios

## Ejemplos de Uso

### Traducir Nombre de NPC

```xml
<localisation id="30001" name="Guardia" title="Protector de la Villa" />
```

### Traducir Mensaje del Sistema

```xml
<localisation id="100" translation="No tienes suficiente dinero." />
```

### Traducir Mensaje Personalizado

```xml
<localisation message="Welcome to the server!" translation="¡Bienvenido al servidor!" />
```

## Notas Importantes

- Los archivos deben estar codificados en **UTF-8** para soportar caracteres especiales (á, é, í, ó, ú, ñ, etc.)
- Los cambios en los archivos XML requieren **reiniciar el servidor**
- Mantener la lista de `SendMessageLocalisation.xml` lo más corta posible (usar SystemMessages cuando sea apropiado)
- Los IDs en `SystemMessageLocalisation.xml` deben coincidir con los IDs del cliente de Lineage II

## Contribuir

Para añadir más traducciones:

1. Identificar el mensaje o NPC que necesita traducción
2. Buscar el ID correspondiente (para NPCs y mensajes del sistema)
3. Añadir la entrada correspondiente en el archivo XML apropiado
4. Probar en el servidor
5. Reportar cualquier problema o mejora

## Soporte

Para más información sobre el sistema multiidioma de L2J Mobius:
- Documentación oficial: https://l2jmobius.org
- Foros de la comunidad: https://l2jmobius.org/forum/
