<template>
  <div>
    <el-row type="flex" justify="end">
      <el-col :xs="24" :sm="24" :md="24" style="text-align: right">
        <el-button id="exporthistories" v-waves :loading="downloadLoading" class="filter-item" type="primary" icon="Download" @click="handleDownload">
          Export
        </el-button>
      </el-col>
    </el-row>
    <el-table
      :ref="reference"
      :data="data"
      :height="height"
      :v-loading="listLoading"
      border
      fit
      highlight-current-row
      style="width: 100%"
      :row-class-name="className"
      @sort-change="sortChange"
    >
      <el-table-column
        v-for="(col, index) in tablecols"
        :key="index"
        :prop="col.prop"
        :label="col.label"
        :sortable="col.sortable"
        :min-width="col.width"
      >
        <template #default="{row}">
          <span>{{ row[col.prop] }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import waves from '@/directive/waves' // waves directive

export default {
  name: 'Historytable',
  directives: { waves },
  props: {
    reference: {
      type: String,
      default: null
    },
    height: {
      type: String,
      default: null
    },
    loading: {
      type: Boolean,
      default: null
    },
    listLoading: {
      type: Boolean,
      default: null
    },
    className: {
      type: String,
      default: null
    },
    sortColumn: {
      type: String,
      default: null
    },
    data: {
      type: [Array, Object],
      default: null
    },
    keyval: {
      type: Number,
      default: 0
    },
    cols: {
      required: true,
      type: Array
    }
  },
  emits: ['sort-change'],
  data() {
    return {
      downloadLoading: false,
      tablecols: this.cols.map(v => {
        v.sortable = 'custom'
        return v
      })
    }
  },
  methods: {
    sortChange(val) {
      this.$emit('sort-change', { column: val.prop, order: val.order })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        var tHeader = []
        var filterVal = []
        this.tablecols.forEach(e => {
          tHeader.push(e.prop)
          filterVal.push(e.prop)
        })
        const data = this.data.map(v => filterVal.map(j => {
          return v[j]
        }))
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: 'table-list'
        })
        this.downloadLoading = false
      })
    }
  }
}
</script>
