<template>
  <div>
    <div class="statistics-grid">
      <div class="stat-card">
        <div class="stat-value">{{ statistics.todayCompleted || 0 }}</div>
        <div class="stat-label">今日完成番茄数</div>
      </div>
      <div class="stat-card secondary">
        <div class="stat-value">{{ formatTotalMinutes(statistics.totalFocusMinutes) }}</div>
        <div class="stat-label">总专注时长</div>
      </div>
    </div>

    <div class="card">
      <h3 style="margin-bottom: 20px; font-size: 20px;">📊 本周完成趋势</h3>
      <div ref="chartRef" class="chart-container"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { recordApi } from '../api/index.js'

const chartRef = ref(null)
let chartInstance = null

const statistics = ref({
  todayCompleted: 0,
  totalFocusMinutes: 0,
  weeklyTrend: []
})

const formatTotalMinutes = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins > 0 ? mins + '分钟' : ''}`
  }
  return `${mins}分钟`
}

const loadStatistics = async () => {
  try {
    const response = await recordApi.getStatistics()
    statistics.value = response.data
    await nextTick()
    initChart()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const initChart = () => {
  if (!chartRef.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  const weekData = statistics.value.weeklyTrend || []
  const dates = weekData.map(item => item.date)
  const completedData = weekData.map(item => item.completed)
  const minutesData = weekData.map(item => item.minutes)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['完成番茄数', '专注时长(分钟)'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        color: '#636e72'
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '番茄数',
        min: 0,
        axisLabel: {
          color: '#636e72'
        }
      },
      {
        type: 'value',
        name: '分钟',
        min: 0,
        position: 'right',
        axisLabel: {
          color: '#636e72'
        }
      }
    ],
    series: [
      {
        name: '完成番茄数',
        type: 'line',
        smooth: true,
        data: completedData,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 107, 107, 0.5)' },
            { offset: 1, color: 'rgba(255, 107, 107, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#ff6b6b',
          width: 3
        },
        itemStyle: {
          color: '#ff6b6b'
        }
      },
      {
        name: '专注时长(分钟)',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: minutesData,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(78, 205, 196, 0.5)' },
            { offset: 1, color: 'rgba(78, 205, 196, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#4ecdc4',
          width: 3
        },
        itemStyle: {
          color: '#4ecdc4'
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance && chartInstance.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  chartInstance && chartInstance.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>
