import React from 'react'
import { ConfigProvider, message } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { RouterProvider } from 'react-router-dom'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import 'antd/dist/reset.css'
import useSWR from 'swr'
import type { AxiosError } from 'axios'
import { router } from './routes/router.tsx'
import type { Information } from './stores/useInformationStore.tsx'
import { useInformationStore } from './stores/useInformationStore.tsx'
import type { HttpResponse } from './lib/Http.tsx'
import { http } from './lib/Http.tsx'

dayjs.locale('zh-cn')
export const App: React.FC = () => {
  const { information, setInformation } = useInformationStore()

  useSWR('/info', async (path) => {
    return http.get<Information>(path)
      .then((res) => {
        if (!information.name) {
          setInformation(res.data.data)
        }
        return res.data.data
      })
      .catch((err: AxiosError<HttpResponse>) => {
        void message.error(err.response?.data.message ?? '获取信息失败')
        throw err
      })
  })
  return (
    <ConfigProvider locale={zhCN}>
      <RouterProvider router={router}/>
    </ConfigProvider>
  )
}
