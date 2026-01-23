# Recursive AGENTS.md Design - Learnings

## Session 1: Initial Refactoring (2026-01-23)

### Decisions Made

1. **Root AGENTS.md Structure**
   - Kept: Project structure overview (universal)
   - Kept: Git usage conventions (universal)
   - Kept: Database conventions (universal)
   - Kept: Development workflow (universal)
   - Kept: Frontend UI/UX guidelines (universal)
   - Removed: All subproject-specific content (river-server, river-ui-admin, river-ecommica sections)
   - Added: Subproject index table with relative paths

2. **river-server/AGENTS.md Structure**
   - Added inheritance declaration at top
   - Added pattern-based framework starter classification (avoiding hardcoding)
   - Added business module classification
   - Added no-reinventing-wheel清单 (pattern-based)
   - Reorganized all existing content under "特有规范" section

3. **river-ui-admin/AGENTS.md Structure**
   - Added inheritance declaration at top
   - Added technology stack classification table
   - Added API calling patterns section
   - Reorganized existing content

4. **river-ecommica/AGENTS.md Structure**
   - Added inheritance declaration at top
   - Added technology stack classification table
   - Added internationalization patterns section
   - Preserved detailed pagination specification (valuable content)

### Conventions Discovered

- Use pattern-based descriptions to avoid hardcoding specific module names
- Relative paths for inheritance (`../AGENTS.md`)
- Subproject index uses relative paths (`river-server/AGENTS.md`)
- Each subproject has a "特有规范" section containing all project-specific content

### Commands Used

- Direct file writes via delegate_task to complete refactoring

### Technical Gotchas

- None encountered during this session

### Issues Encountered

- None - all tasks completed successfully

### Files Modified

1. `/Users/apple/Projects/shixiaohe/river-ad-workspace/AGENTS.md` (68 lines)
2. `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-server/AGENTS.md` (196 lines)
3. `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin/AGENTS.md` (192 lines)
4. `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/AGENTS.md` (278 lines)

### Session Metrics

- Duration: ~2 minutes (parallel execution of 4 tasks)
- Tasks Completed: 4/4
- Lines Changed: ~-100 net (removed 218, added ~124)

---

## Session 2: UI Admin Base Components (2026-01-23)

### Discovery

Searched for base components in river-ui-admin and added them to "禁止重复造轮子清单".

### Components Discovered

**src/components/** (24 components):
- Basic: Button, Icon, Card, ContentWrap
- Form: UploadFile, SelectForm, Editor, FormCreate
- Table: Table, ImportTable
- Chart: Echart
- Advanced: JsonEditor, BpmnProcessDesigner

**src/hooks/web/** (8 hooks):
- useTable, useForm, useCrudSchemas, useTagsView, useCache, useWatermark, useGuide, useValidator

**src/utils/** (9 utilities):
- dict.ts, tree.ts, encrypt.ts, routerHelper.ts, permission.ts, download.ts, formatTime.ts, formRules.ts, dateUtil.ts

### Files Modified

1. `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin/AGENTS.md` (added "禁止重复造轮子清单" section with 3 tables)

### Commands Used

- glob: Searched for component, hook, utils, composables patterns
- grep: Verified component exports and patterns

### Session Metrics

- Duration: ~30 seconds
- Tasks Completed: 1/1
- Lines Added: ~60 lines
