export default function FieldError({ fieldErrors, name }) {
  const message = fieldErrors?.[name]
  if (!message) return null
  return <p className="field-error">{message}</p>
}
