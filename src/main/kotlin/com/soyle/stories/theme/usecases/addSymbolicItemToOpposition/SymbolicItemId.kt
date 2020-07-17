package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import java.util.*

sealed class SymbolicItemId(val itemId: UUID)
class CharacterId(val characterId: UUID) : SymbolicItemId(characterId)
class LocationId(val locationId: UUID) : SymbolicItemId(locationId)
class SymbolId(val symbolId: UUID) : SymbolicItemId(symbolId)