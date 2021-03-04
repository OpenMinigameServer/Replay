package io.github.openminigameserver.replay.helpers

import io.github.openminigameserver.replay.extensions.setEquipmentForEntity
import io.github.openminigameserver.replay.extensions.toMinestom
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.model.recordable.entity.data.EquipmentEntityData
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import net.minestom.server.entity.*
import net.minestom.server.entity.type.animal.*
import net.minestom.server.entity.type.decoration.EntityArmorStand
import net.minestom.server.entity.type.decoration.EntityItemFrame
import net.minestom.server.entity.type.monster.*
import net.minestom.server.entity.type.other.EntityAreaEffectCloud
import net.minestom.server.entity.type.other.EntityEndCrystal
import net.minestom.server.entity.type.other.EntityIronGolem
import net.minestom.server.entity.type.other.EntitySnowman
import net.minestom.server.entity.type.projectile.EntityEyeOfEnder
import net.minestom.server.entity.type.projectile.EntityPotion
import net.minestom.server.entity.type.vehicle.EntityBoat
import net.minestom.server.inventory.EquipmentHandler
import net.minestom.server.utils.Position
import org.apache.commons.lang3.reflect.ConstructorUtils
import java.util.*

internal object EntityHelper {

    private val entityTypeMap = mutableMapOf<EntityType, Class<out Entity>>()

    @JvmStatic
    fun init() {
        entityTypeMap[EntityType.POTION] = EntityPotion::class.java
        entityTypeMap[EntityType.CAVE_SPIDER] = EntityCaveSpider::class.java
        entityTypeMap[EntityType.SILVERFISH] = EntitySilverfish::class.java
        entityTypeMap[EntityType.COW] = EntityCow::class.java
        entityTypeMap[EntityType.BLAZE] = EntityBlaze::class.java
        entityTypeMap[EntityType.ZOMBIFIED_PIGLIN] = EntityZombifiedPiglin::class.java
        entityTypeMap[EntityType.PANDA] = EntityPanda::class.java
        entityTypeMap[EntityType.ARMOR_STAND] = EntityArmorStand::class.java
        entityTypeMap[EntityType.GIANT] = EntityGiant::class.java
        entityTypeMap[EntityType.PHANTOM] = EntityPhantom::class.java
        entityTypeMap[EntityType.GHAST] = EntityGhast::class.java
        entityTypeMap[EntityType.BEE] = EntityBee::class.java
        entityTypeMap[EntityType.SPIDER] = EntitySpider::class.java
        entityTypeMap[EntityType.EXPERIENCE_ORB] = ExperienceOrb::class.java
        entityTypeMap[EntityType.ITEM] = ItemEntity::class.java
        entityTypeMap[EntityType.ITEM_FRAME] = EntityItemFrame::class.java
        entityTypeMap[EntityType.END_CRYSTAL] = EntityEndCrystal::class.java
        entityTypeMap[EntityType.SNOW_GOLEM] = EntitySnowman::class.java
        entityTypeMap[EntityType.RABBIT] = EntityRabbit::class.java
        entityTypeMap[EntityType.WITCH] = EntityWitch::class.java
        entityTypeMap[EntityType.ENDERMITE] = EntityEndermite::class.java
        entityTypeMap[EntityType.GUARDIAN] = EntityGuardian::class.java
        entityTypeMap[EntityType.EYE_OF_ENDER] = EntityEyeOfEnder::class.java
        entityTypeMap[EntityType.POLAR_BEAR] = EntityPolarBear::class.java
        entityTypeMap[EntityType.OCELOT] = EntityOcelot::class.java
        entityTypeMap[EntityType.CAT] = EntityCat::class.java
        entityTypeMap[EntityType.CHICKEN] = EntityChicken::class.java
        entityTypeMap[EntityType.IRON_GOLEM] = EntityIronGolem::class.java
        entityTypeMap[EntityType.BOAT] = EntityBoat::class.java
        entityTypeMap[EntityType.AREA_EFFECT_CLOUD] = EntityAreaEffectCloud::class.java
        entityTypeMap[EntityType.ZOMBIE] = EntityZombie::class.java
        entityTypeMap[EntityType.DOLPHIN] = EntityDolphin::class.java
        entityTypeMap[EntityType.FOX] = EntityFox::class.java
        entityTypeMap[EntityType.PIG] = EntityPig::class.java
        entityTypeMap[EntityType.LLAMA] = EntityLlama::class.java
        entityTypeMap[EntityType.CREEPER] = EntityCreeper::class.java
        entityTypeMap[EntityType.ARMOR_STAND] = EntityArmorStand::class.java
        entityTypeMap[EntityType.SLIME] = EntitySlime::class.java
        entityTypeMap[EntityType.MOOSHROOM] = EntityMooshroom::class.java
    }

    fun createEntity(
        type: EntityType,
        spawnPosition: Position,
        entityData: BaseEntityData?,
        isAutoViewable: Boolean
    ): Entity =
        (if (type == EntityType.PLAYER && entityData is PlayerEntityData) {
            ReplayPlayerEntity(UUID.randomUUID(), entityData.userName, entityData.metadata).also {
                it.skin = entityData.skin?.toMinestom()
            }
        } else {
            val entityTypeClazz = entityTypeMap[type]

            if (entityTypeClazz != null) {
                ConstructorUtils.invokeConstructor(entityTypeClazz, spawnPosition)
            } else {
                object : EntityCreature(type, spawnPosition) {
                    override fun update(time: Long) {
                    }

                    override fun spawn() {
                    }
                }
            }

        }).also {
            it.isAutoViewable = isAutoViewable
            it.setNoGravity(true)
            if (it is LivingEntity) {
                it.health = it.maxHealth
            }

            if (it is EquipmentHandler && entityData is EquipmentEntityData) {
                it.setEquipmentForEntity(entityData.equipment)
            }
        }

}