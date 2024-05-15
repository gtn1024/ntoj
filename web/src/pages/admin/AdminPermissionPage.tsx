import React, { Fragment, useEffect, useState } from 'react'
import { Button, message } from 'antd'
import type { AxiosError } from 'axios'
import c from 'classnames'
import { useRoles } from '../../hooks/useRoles.ts'
import { PERMS_BY_FAMILY, checkPermission } from '../../lib/Permission.ts'
import type { HttpResponse } from '../../lib/Http.tsx'
import { http } from '../../lib/Http.tsx'

export const AdminPermissionPage: React.FC = () => {
  const { roles } = useRoles()
  const [data, setData] = useState<Record<string, bigint> | null>(null)
  const [sortedData, setSortedData] = useState<Array<string> | null>(null)
  const sortRoles = (roles: Record<string, bigint>) => {
    let tmp1: Array<string> = []
    tmp1.push('guest')
    tmp1.push('default')
    const tmp2: Array<string> = []
    for (const role in roles) {
      if (role === 'root' || role === 'guest' || role === 'default') {
        continue
      }
      tmp2.push(role)
    }
    tmp2.sort((a, b) => a.localeCompare(b))
    tmp1 = tmp1.concat(tmp2)
    tmp1.push('root')
    return tmp1
  }
  useEffect(() => {
    if (!data && roles) {
      setData(roles)
      setSortedData(sortRoles(roles))
    }
  }, [roles])
  const onClickSave = () => {
    const reqData: Record<string, string> = {}
    for (const role in data) {
      if (role === 'root') {
        continue
      }
      reqData[role] = data[role].toString()
    }
    http.patch('/permission', reqData)
      .then(() => {
        void message.success('保存成功')
      })
      .catch((e: AxiosError<HttpResponse>) => {
        void message.error(e.response?.data.message || '保存失败')
      })
  }
  return (
    <div className="flex flex-col p-4">
      <h2 className="mb-2">权限管理</h2>
      <div className="w-full">
        <table className="mb-2 w-full">
          <thead>
            <tr>
              <th className="py-4">权限</th>
              {
                sortedData?.map(role => (
                  <th key={role} className="py-4">{role}</th>
                ))
              }
            </tr>
          </thead>
          <tbody>
            {
            Object.keys(PERMS_BY_FAMILY).map(family => (
              <Fragment key={family}>
                <tr>
                  <td colSpan={Object.keys(data || {}).length + 1} className="bg-blue-200 py-2 pl-2 font-bold">
                    {family}
                  </td>
                </tr>
                {
                  PERMS_BY_FAMILY[family].map((perm, index) => (
                    <tr key={perm.value} className={c(index % 2 ? 'bg-white' : 'bg-[#f4f4f4]')}>
                      <td className="py-2 pl-4">{perm.desc}</td>
                      {
                        sortedData?.map(role => (
                          <td key={role} className="py-2 text-center">
                            <input
                              type="checkbox"
                              disabled={role === 'root'}
                              defaultChecked={checkPermission(data?.[role] || 0n, perm.value)}
                              onChange={(e) => {
                                if (role === 'root')
                                  return
                                if (e.target.checked) {
                                  setData({
                                    ...data,
                                    [role]: (data?.[role] || 0n) | perm.value,
                                  })
                                } else {
                                  setData({
                                    ...data,
                                    [role]: (data?.[role] || 0n) & ~perm.value,
                                  })
                                }
                              }}
                            />
                          </td>
                        ))
                      }
                    </tr>
                  ))
                }
              </Fragment>
            ))
          }
          </tbody>
        </table>
        <Button
          type="primary"
          onClick={onClickSave}
        >
          保存
        </Button>
      </div>
    </div>
  )
}
