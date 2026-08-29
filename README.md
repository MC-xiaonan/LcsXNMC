# LcsXNMC

一个基于 [PaperMC](https://papermc.io/)（`paperweight-patcher`/`paperweight-core` 分支机制）的定制 Minecraft Java 版服务端，目标是融合 Bukkit / Paper / Leaves / Folia 生态的 API 能力，并提供一套自研的 **LcsXNMC API**，在不破坏现有 Paper/Bukkit 插件兼容性的前提下扩展服务端能力。

- Minecraft 版本：`26.2`
- 构建方式：Gradle + paperweight（与 Paper 官方分支结构一致，源码以补丁（patch）形式维护在 `paper-patches/` / `minecraft-patches/` 下）
- 许可证：继承自上游 Paper/Spigot/Bukkit/CraftBukkit，[GNU GPL v3](licenses/GPL.md)，详见 [LICENSE.md](LICENSE.md)

## 项目结构

```
lcsxnmc-api/         # 自研 LcsXNMC API（面向插件开发者的公开接口）+ Paper API 补丁
lcsxnmc-server/       # paperweight-patcher 项目：服务端补丁、构建脚本、Minecraft/Paper 源码补丁
lcsxnmc-checkstyle/   # 代码风格检查规则
paper-api/            # (生成目录，由补丁应用生成，已加入 .gitignore)
paper-server/         # (生成目录，由补丁应用生成，已加入 .gitignore)
```

`paper-api/`、`paper-server/`、`lcsxnmc-server/src/minecraft/java` 等目录是由 `lcsxnmc-server/*-patches/` 下的补丁文件应用生成的**嵌套 Git 仓库**，不直接提交源码全量，而是提交补丁差异。

## 已完成的功能阶段

- **Phase 0-3**：基础分支搭建、LcsXNMC API 框架、并行计算卸载执行器（`LcsXNMCComputeExecutor`）等安全可并行优化。
- **Phase 4.1**：通用可扩展的“遗留行为提供者”框架（`LegacyBehaviorProvider`），供服主/插件开发者自行恢复被上游移除的旧版本行为。
- **Phase 4.2**：红石保真度审计（对齐原版时序，排查历史已知的红石相关 bug）。
- **Phase 4.3**：计算卸载执行器 + 异步调度扩展。
- **Phase 4.4a（区域多线程兼容层，进行中）**：
  - 区域聚类算法（`RegionClusterer`，按在线玩家做粘性 ID 聚类）。
  - `TickThread`/`RegionWorkerThread`/`TickRegion` 真实区域归属校验骨架，`EntityLookup.moveEntity` 两阶段跨区域实体交接协议。
  - 并发加固：`ChunkMap.entityMap`、`Scoreboard` 内部映射同步化，`ServerWaypointManager` 整实例互斥。
  - 全程保持 `region-threading.enabled=false`，对现有单线程行为零影响；真正的区域并行 tick 循环集成（`RegionizedWorldData`/`FallbackRegionScheduler` 真派发）尚在规划中，未来版本发布。

## 构建

需要 JDK 21（Gradle 工具链）。

```bash
./gradlew createPaperclipJar
```

生成的 Paperclip 启动 jar 位于 `lcsxnmc-server/build/libs/lcsxnmc-paperclip-*.jar`。

修改 `paper-server`、`paper-api`、`lcsxnmc-server/src/minecraft/java` 下的嵌套仓库源码后，需要先在对应嵌套仓库内提交（`git commit`），再在仓库根目录运行：

```bash
bash rb.sh
```

重新生成外层仓库的补丁文件（`*-patches/features/*.patch`），随后提交补丁文件到本仓库。

## 兼容性说明

- 目标是完整兼容现有 Paper/Bukkit 插件生态；区域多线程相关的新增能力在正式启用前会经过严格的“默认关闭 + 零行为变化”验证。
- 未声明支持 Folia 的插件在区域多线程正式启用后仍会被保护性钉扎到单一线程执行（尚未实现，见开发计划）。

## 免责声明

本项目为个人/社区自研分支，与 Mojang、Microsoft、PaperMC 官方无关联。使用前请自行评估稳定性与兼容性风险。
